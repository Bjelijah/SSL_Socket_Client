
#ifndef __H265_DEMO_CALLBACKFUNC_H__  /* Macro sentry to avoid redundant including */
#define __H265_DEMO_CALLBACKFUNC_H__

#ifdef __cplusplus
extern "C"{
#endif 

extern INT32 g_MallocCnt;
extern INT32 g_MallocSize;
extern INT32 g_FreeCnt;

void HW265D_Log( UINT32 channel_id, IHWVIDEO_ALG_LOG_LEVEL eLevel, INT8 *p_msg, ...);
void *HW265D_Malloc(UINT32 channel_id, UINT32 size);
void HW265D_Free(UINT32 channel_id, void * ptr);

#ifdef __cplusplus
}

#endif  /* __cplusplus */

#endif  /* __H265_DEMO_CALLBACKFUNC_H__ */


