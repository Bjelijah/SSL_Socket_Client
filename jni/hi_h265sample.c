#include <include/CallBack.h>
#include <stdio.h>
#include <malloc.h>
#ifdef _MSC_VER
#include <Windows.h>
#include <time.h>
#else
#include <time.h>
#include <sys/time.h> 
#endif

//#include <include/IHWVideo_Typedef.h>
//#include <include/IHW265Dec_Api.h>

#include "IHWVideo_Typedef.h"
#include "IHW265Dec_Api.h"

#define TEST_MULTITHREAD 0

void *HW265D_Malloc(UINT32 channel_id, UINT32 size) 
{
	return (void *)malloc(size);
}


void HW265D_Free(UINT32 channel_id, void * ptr) 
{
	free(ptr);
}

void HW265D_Log( UINT32 channel_id, IHWVIDEO_ALG_LOG_LEVEL eLevel, INT8 *p_msg, ...)
{
}

INT32 H265DecLoadAU(UINT8* pStream, UINT32 iStreamLen, UINT32* pFrameLen) 
{ 
    UINT32 i; 
    UINT32 state = 0xffffffff; 
    BOOL32 bFrameStartFound=0;
	BOOL32 bSliceStartFound = 0;

    *pFrameLen = 0;
    if( NULL == pStream || iStreamLen <= 4) 
    {  
        return -1; 
    }

    for( i = 0; i < iStreamLen; i++) 
    { 
        if( (state & 0xFFFFFF7E) >= 0x100 &&
            (state & 0xFFFFFF7E) <= 0x13E )
        { 
            if( 1 == bFrameStartFound || bSliceStartFound == 1 ) 
            { 
                if( (pStream[i+1]>>7) == 1)
                { 
                    *pFrameLen = i - 4; 
                    return 0;
                }
            } 
            else 
            { 
				bSliceStartFound = 1;
                //bFrameStartFound = 1; 
            } 
        } 

        /*find a vps, sps, pps*/ 
        if( (state&0xFFFFFF7E) == 0x140 || 
            (state&0xFFFFFF7E) == 0x142 || 
            (state&0xFFFFFF7E) == 0x144)
        { 
			if (1 == bSliceStartFound)
			{
				bSliceStartFound = 1;
			}
            else if(1 == bFrameStartFound) 
            { 
                *pFrameLen = i - 4;
                return 0; 
            } 
            else 
            { 
                bFrameStartFound = 1; 
            } 
        } 

        state = (state << 8) | pStream[i];
    } 

    *pFrameLen = i; 
    return -1;
}

INT64 GetTime_ms( void )
{
#ifdef _MSC_VER
	LARGE_INTEGER m_nFreq;
	LARGE_INTEGER m_nTime;

	QueryPerformanceFrequency(&m_nFreq);
	QueryPerformanceCounter(&m_nTime);

	return m_nTime.QuadPart*1000/m_nFreq.QuadPart;

#elif defined(__GNUC__)
	struct timeval tv_date;
	gettimeofday( &tv_date, NULL );
	return (INT64)tv_date.tv_sec * 1000 + (INT64)tv_date.tv_usec;
#endif
}


int main(int argc, unsigned char** argv) // for DecFrame Mode
{
	FILE *fpInFile = NULL;	
	FILE *fpOutFile = NULL;	
	INT32 iRet = 0;
	INT32 iInputParam;
	UINT8 *pInputStream = NULL, *pStream;
	UINT32 uiChannelId = 0x00112233;
	UINT32 iFrameIdx = 0;

	BOOL32 bStreamEnd = 0;
	INT32 iFileLen;
	INT32 LoadCount = 0;

	INT32 time;
	INT64 StartTime, EndTime;

	IH265DEC_HANDLE hDecoder = NULL;
	IHW265D_INIT_PARAM stInitParam = {0};
	IH265DEC_INARGS stInArgs;
	IH265DEC_OUTARGS stOutArgs = {0};
	IHWVIDEO_ALG_VERSION_STRU stVersion;

	INT32 MultiThreadEnable = 0;	// default is single thread mode
	INT32 DispOutput = 0;           

	if (IHW265D_OK == IHW265D_GetVersion(&stVersion))
	{
		fprintf(stderr, "Version: %s\nReleaseTime %s\n\n", stVersion.cVersionChar, stVersion.cReleaseTime);
	}

	if (argc < 1)
	{
		fprintf(stderr, "Comand Example:\n");
		fprintf(stderr, "hi_h265_dec_w.exe stream_file.h265 [-i streamfile] [-o yuvfile] \n\n");
		return 0;
	}

	/* parse optional parameters */
	for(iInputParam = 1; iInputParam<argc; iInputParam++)
	{
		if (strcmp("-i", argv[iInputParam]) == 0 && (iInputParam+1)<argc)
		{
			fpInFile = fopen(argv[++iInputParam], "rb");
			if (NULL == fpInFile)
			{
				fprintf(stderr, "Unable to open a h265 stream file %s \n", argv[iInputParam]);
				goto exitmain;
			}
			printf("decoding file: %s...\n",argv[iInputParam]);
		}
		else if(strcmp("-o", argv[iInputParam]) == 0 && (iInputParam+1)<argc)
		{
			/* open yuv file */
			fpOutFile = fopen(argv[++iInputParam], "wb");
			if (NULL == fpOutFile)
			{
				fprintf(stderr, "Unable to open the file to save yuv %s.\n", argv[iInputParam]);
				goto exitmain;
			}
			printf("save yuv file: %s...\n",argv[iInputParam]);
		}
	}

#ifndef TEST_MULTITHREAD
	MultiThreadEnable = 0;//signal thread
#else
	MultiThreadEnable = 1;//multithread
#endif

	fseek( fpInFile, 0, SEEK_END);
	iFileLen = ftell( fpInFile);
	fseek( fpInFile, 0, SEEK_SET);

	pInputStream = (unsigned char *) malloc(iFileLen);
	if (NULL == pInputStream)
	{
		fprintf(stderr, "Unable to malloc stream buffer (Size %d).\n", iFileLen);
		goto exitmain;
	}

	fread(pInputStream, 1, iFileLen, fpInFile);
	pStream = pInputStream;

	/*create decode handle*/
	{
		stInitParam.uiChannelID = 0;
		stInitParam.iMaxWidth   = 1920;
		stInitParam.iMaxHeight  = 1088;
		stInitParam.iMaxRefNum  = 4;

		stInitParam.eThreadType = MultiThreadEnable? IH265D_MULTI_THREAD: IH265D_SINGLE_THREAD;
		stInitParam.eOutputOrder= DispOutput? IH265D_DISPLAY_ORDER:IH265D_DECODE_ORDER;

		stInitParam.MallocFxn  = HW265D_Malloc;
		stInitParam.FreeFxn    = HW265D_Free;
		stInitParam.LogFxn     = HW265D_Log;
	}
	iRet = IHW265D_Create(&hDecoder, &stInitParam);
	if (IHW265D_OK != iRet)
	{
		fprintf(stderr, "Unable to create decoder.\n");
		goto exitmain; 
	}

	/* count decoding time: start */
	StartTime = GetTime_ms();

	while(!bStreamEnd)
	{
		INT32 iNaluLen;
		H265DecLoadAU(pStream, iFileLen, &iNaluLen);

		stInArgs.eDecodeMode =  iNaluLen>0 ? IH265D_DECODE : IH265D_DECODE_END;
		stInArgs.pStream = pStream;
		stInArgs.uiStreamLen = iNaluLen;

		pStream += iNaluLen;
		iFileLen-= iNaluLen;
		LoadCount++;

		stOutArgs.eDecodeStatus = -1;
		stOutArgs.uiBytsConsumed = 0;

		// if return value if IH265D_NEED_MORE_BITS, read more bits from files
		while(stOutArgs.eDecodeStatus != IH265D_NEED_MORE_BITS)
		{
			// decode end
			if(stOutArgs.eDecodeStatus == IH265D_NO_PICTURE)
			{
				bStreamEnd = 1;
				break;
			}
			// output decoded pictures
			if (stOutArgs.eDecodeStatus == IH265D_GETDISPLAY)
			{
				// write output YUV to files
				if (fpOutFile != NULL)
				{
					UINT32 i;
					for (i=0;i<stOutArgs.uiDecHeight;i++)
					{
						fwrite(stOutArgs.pucOutYUV[0]+i*stOutArgs.uiYStride, 1, stOutArgs.uiDecWidth, fpOutFile);
					}
					for (i=0;i<((stOutArgs.uiDecHeight)>>1);i++)
					{
						fwrite(stOutArgs.pucOutYUV[1]+i*stOutArgs.uiUVStride, 1, stOutArgs.uiDecWidth>>1, fpOutFile);
					}
					for (i=0;i<((stOutArgs.uiDecHeight)>>1);i++)
					{
						fwrite(stOutArgs.pucOutYUV[2]+i*stOutArgs.uiUVStride, 1, stOutArgs.uiDecWidth>>1, fpOutFile);
					}
				}
				iFrameIdx++;
			}

			// decode bins
			{
				stInArgs.pStream += stOutArgs.uiBytsConsumed;
				stInArgs.uiStreamLen -= stOutArgs.uiBytsConsumed;

				iRet = IHW265D_DecodeFrame(hDecoder, &stInArgs, &stOutArgs);


				if ((iRet != IHW265D_OK) && (iRet != IHW265D_NEED_MORE_BITS))
				{
					fprintf(stderr, "ERROR: IHW265D_DecodeFrame failed!\n");

					if (0 == iFileLen)
					{
						bStreamEnd = 1;
					}
					break;
				}
			}
		}
	}

	printf("Hello world!\n");
	/* count decoding time: end */
	EndTime = GetTime_ms();
	time = (INT32)(EndTime-StartTime);
	printf("\n uiDecWidth = %d, uiDecHeight = %d, time= %d ms\n", stOutArgs.uiDecWidth, stOutArgs.uiDecHeight, time);
	printf("%d frames\n",iFrameIdx);
	printf("fps: %d\n", iFrameIdx*1000/(time+1));

exitmain:
	if (fpInFile != 0)		fclose(fpInFile);
	if (fpOutFile != 0)     fclose(fpOutFile);
	if (hDecoder != NULL)	IHW265D_Delete(hDecoder);

	if (pInputStream != NULL) 
	{   
		free(pInputStream);
		pInputStream = NULL;
	}

	return 0;
}
