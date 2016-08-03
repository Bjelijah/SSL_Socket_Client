#ifndef _PROTOCOL_TYPE_EX_H
#define _PROTOCOL_TYPE_EX_H

#include <stdint.h>
#include "protocol_type.h"

#define HW_FRAME_FOCUS_NOTIFY_FRAME		20	/* jzh: 聚焦通知帧类型 */
#define HW_FRAME_ANALYSE_FRAME	21	/* jzh: 分析数据帧 */
#define HW_FRAME_MISC_DATA_FRAME		22	/* jzh: extra_data主码流 */
#define HW_FRAME_MISC_DATA_SUB_FRAME	23	/* jzh: extra_data次码流 */

/* 设备ID设置 */
#define HW_EXTEND_PROTOCOL_SET_ID 0xff001000
struct tSerialID {
	char serial_id[32];
	uint8_t reserve[32];
};

/* jzh: mac设置 */
#define HW_EXTEND_PROTOCOL_GET_MAC (0xff001010)
#define HW_EXTEND_PROTOCOL_SET_MAC (0xff001011)
typedef struct {
	char mac[32]; //字符串
	unsigned char reserved[32];
}net_mac_t;


/* jzh: work mode for net protocol, 2009-07-17 */
#define HW_EXTEND_PROTOCOL_SET_WORKMODE 0xff001020
#define HW_EXTEND_PROTOCOL_GET_WORKMODE 0xff001021
struct tWorkmode {
	/* 是否启用 */
	int32_t enable;

	/* 模式 */
	int32_t mode;

	/* 预设模式起始时间和结束时间 */
	int32_t start_hour; 
	int32_t start_min; 
	int32_t end_hour; 
	int32_t end_min; 

	int sensitivity; //自动模式下的灵敏度 (0~100)

	/* 夜间参数 */
	struct tVideoColor color;
	int32_t eshutter;
	int32_t agc;

	/* 白天色彩 */
	struct tVideoColor day_color;

	uint8_t reserve[16];
};


/* 强光抑制 */
#define HW_EXTEND_PROTOCOL_GET_BLACK_MASK_BLC	0xff001022
#define HW_EXTEND_PROTOCOL_SET_BLACK_MASK_BLC	0xff001023
struct tBlackMaskBlc{
	int slot;

	int enable;

	/* 夜间的luma最大值,小于该值则启用逆光抑制 */
	int max_luma_night;		

	/* 调整后，原高亮block的y值应该在此范围内  */
	int block_y_max;
	int block_y_min;		

	/* 调整后如果y值小于该值，说明亮光过去，需要恢复自动曝光 */
	int block_y_night_normal;	

	int mode; //0: 固定增益 1:固定快门 2:自动
	int fixedAgc;
	int fixedEshutter;

	int max_eshutter;
	int min_eshutter;
	int max_agc;
	int min_agc;
	
	uint32_t area_map;

	/* 设置掩码 */
	/* 0: nothing to set */
	/* 1<<0: enable */
	/* 1<<1: area_map */
	/* 1<<2: max_luma_night */
	/* 1<<3: block_y_max */
	/* 1<<4: block_y_min */
	/* 1<<5: block_y_night_normal */
	/* 1<<6: mode */
	/* 1<<7: fixedAgc */
	/* 1<<8: fixedEshutter */
	/* 1<<9: max_eshutter */
	/* 1<<10: min_eshutter */
	/* 1<<11: max_agc */
	/* 1<<12: min_agc */
	uint32_t set_mask;

	uint8_t reserve[32];
};

/* real video for nvr */
#define HW_EXTEND_PROTOCOL_GET_REAL_VIDEO   0xff001024

/* 数据迁移参数设置 */
#define HW_EXTEND_PROTOCOL_GET_DATA_TRANSFER 0xff001026
#define HW_EXTEND_PROTOCOL_SET_DATA_TRANSFER 0xff001027
typedef struct {
	uint32_t slot;

	uint32_t enable;

	/* 录像的码流 0:主码流 1:次码流 */
	uint32_t stream; 

	/* 录像帧数，0:满帧率 1-24  */
	uint32_t framerate; 

	/* 是否循环覆盖 */
	uint32_t b_loop_write;
	
	uint8_t reserve[32];

}data_transfer_net_value_t;

/* 数据迁移完成后，nvr发送该命令删除ipcam上的录像数据 
 * use struct tRecFile 
 */
#define HW_EXTEND_PROTOCOL_DELETE_FILES	0xff001028

/* 获取帧索引文件
 * use struct tRecFile 
 */
#define HW_EXTEND_PROTOCOL_GET_INDEX_FILE 0xff001029

/* 功能配置 */
#define HW_EXTEND_PROTOCOL_GET_FUNCTION 0xff001030
#define HW_EXTEND_PROTOCOL_SET_FUNCTION 0xff001031
struct tFunction {
#if 0
	/** 功能列表
	 *  function使用位来记录 
	 */
	typedef enum {
		FUNC_NET_TRANSFER=0,
		FUNC_TWO_STREAMS,
		FUNC_WEB,
		FUNC_3A,
		FUNC_MOTION_DETECTION,
		FUNC_AUDIO,
		FUNC_RS485,
		FUNC_RECORD,		//录像
		FUNC_BNC,		//模拟输出
		FUNC_MANUAL_ESHUTTER,	//手动调节快门
		FUNC_NIGHT_MODE,	//夜间模式
		FUNC_DATA_TRANSFER,	//数据迁移
		FUNC_SMART_SEARCH,	//智能搜索(对ipcam来说,即是否发送移动侦测帧)
		FUNC_BMB,		//强光抑制(Black Mask BLC)
		FUNC_VIDEO_ANALYSE,	//视频分析
		FUNC_POE,		//POE供电
		FUNC_WIFI,		//WIFI
	}function_e;
#endif
	uint32_t function;
	uint8_t reserve[32];
};

/* 可靠校时 */
#define HW_EXTEND_PROTOCOL_GET_TIME 0xff001040 //请求数据长度0，返回time_t的数据
#define HW_EXTEND_PROTOCOL_SET_TIME 0xff001041
typedef struct {
	/*
	 * 是否强制校时
	 * 0: 需要验证当前时间和pre_t的时间是否在force_interval内
	 * 1: 强制校时 
	 */
	uint32_t enable_force;

	/* 误差间隔，单位s */
	uint32_t force_interval;
	
	/* 基准时间，客户端从GET_TIME协议中获取,1970年至今的秒数 */
	uint32_t pre_t;
	
	/* 需要校时的时间 */
	HWSYSTEMTIME cur_t;

	uint8_t reserve[32];
}net_sync_time_t;

/* 设置型号 */
#define HW_EXTEND_PROTOCOL_SET_MODEL_NAME 0xff001045
typedef struct {
	char model[64];
	uint8_t reserver[32];
}net_model_name_t;

/* 摄像机的一些设置 */
#define HW_EXTEND_PROTOCOL_GET_IPCAM_MISC 0xff001047
#define HW_EXTEND_PROTOCOL_SET_IPCAM_MISC 0xff001048
typedef struct {		/* 各项请参考ipcam_misc_t */
	uint32_t flag;		/* 具体设置哪项，由位表示,以下各项按顺序从0开始 */
	uint8_t is_flip;		
	uint8_t enable_lowest_shutter; 
	uint8_t shutter;
	uint8_t noise_filter_auto;
	uint8_t noise_filter_level;
	uint8_t sharp_auto;
	uint8_t sharp_level;
	uint8_t envirenment; /* 0:outdoor 1:indoor */
	uint8_t enable_uppest_agc;
	uint8_t agc_upper_limit;
	uint8_t gamma;				//0~9
	uint8_t reserver[53];
} net_ipcam_misc_t;

/* 聚焦通知 */
#define HW_EXTEND_PROTOCOL_START_FOCUS_NOTIFY		0xff001049
#define HW_EXTEND_PROTOCOL_STOP_FOCUS_NOTIFY		0xff00104a
typedef struct {
	uint32_t slot;
	uint8_t reserver[64];
} NetFocusNotify;

typedef struct {
	uint32_t total_area;	/* 总区域数 */
	uint32_t area;		/* 位变量,0:区域无效 1:有效 */
	uint32_t value[16];		/* 16个区域的清晰度值 */
} NetFocusNofiyFrame;


/* 黑白模式 */
#define HW_EXTEND_PROTOCOL_SET_BLACK_WHITE			0xff00104b
#define HW_EXTEND_PROTOCOL_GET_BLACK_WHITE			0xff00104c
typedef struct {
	uint32_t enable;			/* 是否启动 */
	uint32_t sense;			/* 灵敏度 1~3 */
	uint8_t reserver[64];
} NetBlackWhite;

#define HW_EXTEND_PROTOCOL_START_USB_SEND			0xff00104e
typedef struct {
	uint8_t data_type;			/* 1:YUV 2:MJPEG */
	uint8_t reserver[32];
} NetUsbSend;
#define HW_EXTEND_PROTOCOL_STOP_USB_SEND			0xff00104f


#define HW_EXTEND_PROTOCOL_GET_SNMP_CFG 0xff001050
#define HW_EXTEND_PROTOCOL_SET_SNMP_CFG 0xff001051
typedef struct {
	char ip[IP_LENGTH];
	uint32_t cycle;			/* 1~300s */
	uint8_t reserve[32];
} NetSnmpCfg;

/* 校准 */
#define HW_EXTEND_PROTOCOL_CALIBRATION 0xff001052

#define HW_EXTEND_PROTOCOL_AUTO_FOCUS_START 0xff001060 
#define HW_EXTEND_PROTOCOL_AUTO_FOCUS_STOP	0xff001061 
typedef struct {
	int slot;
	uint8_t custom_focus;	/* 0:全屏聚焦 1:自定义区域聚焦 */
	uint8_t x,y,w,h;		/* 聚焦区域,为8x5的块坐标 */
	uint8_t reserve[32];
} NetAutoFocus;

/* profile */
#define HW_EXTEND_PROTOCOL_GET_PROFILE 0xff001062
#define HW_EXTEND_PROTOCOL_SET_PROFILE 0xff001063
typedef struct {
	uint8_t ipcam_type;         	/* 摄像机类型: 0:普通 1:自动聚焦 2:球机 3:一体机 */
	uint8_t vin_720p;           	/* vin设置为720p， 0:off 1:on */
	uint8_t fix_D1;           	/* 强制主码流为D1编码 0:off 1:on */
	uint8_t use_60fps;          	/* 60帧编码 0:off 1:on */
	uint8_t sensor;         	/* sensor: 0:mt9p031 1:ov2710 2:ov9710 3:mt9m034 */
	uint8_t hw_ir;			/* 1:硬件红外 0:否 */
	uint8_t video_standard;		/* 视频标准: 0:pal 1:ntsc 2:60fps 3:5M pixels */
	uint8_t reserve[30];
} NetProfile;


/* 球机/一体机数据校准 */
#define HW_EXTEND_PROTOCOL_SPEEDDOME_CALI 0xff001065
typedef struct {
	uint8_t cmd;								/* 1:请求 2:校准 3:结束 */
	/***** 以下数据只在cmd==2时起作用 *******/
	/* sub_cmd:
	 * 1:走到某个点(由arg表示 1:wide 2:flangeback 3:tele) 
	 * 2:auto_focus
	 * 3:manual_focus (由arg表示 0:near 1:far)
	 * 0xff: comfirm (确认)
	 */
	uint8_t sub_cmd;                            /* 1,2,3,0xff */
	uint8_t arg;
	uint8_t arg2;
	/* ************************ */
	uint8_t reserve[64];
} NetSpeeddomeCali;

/* 带后焦的camera复位 */
#define HW_EXTEND_PROTOCOL_BACK_FOCAL_RESET 0xff001066

/* 球机设置 */
#define HW_EXTEND_PROTOCOL_GET_SPEEDDOME_CFG 0xff001067
#define HW_EXTEND_PROTOCOL_SET_SPEEDDOME_CFG 0xff001068
typedef struct {
	uint32_t flag;                              /* 位变量,该位设1才起作用 */

	/* 掩码位: flag.0
	 * zoom_track_mode: 
	 *		0:快速模式，中间无停顿 
	 *		1:准确模式，中间有停顿 
	 */
	uint8_t zoom_track_mode;                   

	uint8_t reserve[64];
} NetSpeeddomeCfg;

#define HW_EXTEND_PROTOCOL_SET_ANALYSE_DATA 0xff001070
typedef struct {
	uint32_t slot;
	uint32_t data_len;
	uint8_t timestamp[8];
	uint8_t reserve[24];
} NetAnalyseData;

/* extra_data结构定义 */
typedef struct
{
	uint32_t dev_type;//所属设备

	//视频属性
	uint32_t v_type;//视频类型 0:h264 1:mjpeg
	uint32_t v_w;//视频宽
	uint32_t v_h;//视频高
	uint32_t v_fr;//视频帧率
	uint32_t v_vbr;//是否动态码流
	uint32_t v_max_bps;//视频最大码流
	uint32_t v_bright;//亮度
	uint32_t v_constrast;//对比度
	uint32_t v_saturation;//饱和度
	uint32_t v_hue;//色度
	uint32_t v_reserve[16];

	//摄像机专有属性
	uint32_t ipc_bae;//是否启动自动曝光
	uint32_t ipc_bai;//是否自动光圈
	uint32_t ipc_eshutter;//快门
	uint32_t ipc_agc;//增益
	uint32_t ipc_luma;//流明
	uint32_t ipc_noise_filter;//噪声抑制0-6,6最大
	uint32_t ipc_sharpen;//锐度0-255，255最大
	uint32_t ipc_gamma;//gamma (0~9)
	uint32_t ipc_reserve[16];

	//音频属性
	uint32_t a_type;//音频类型 0:g711u 1:g711a
	uint32_t a_bits;//音频位数
	uint32_t a_chn;//音频通道数
	uint32_t a_sample;//音频采样率
	uint32_t a_reserve[16];

	uint32_t reserve[16];
} NetExtraData;

typedef struct backfocal_pos {
	uint32_t pos;
	uint32_t min_pos;
	uint32_t max_pos;
	uint32_t reserve[8];
} backfocal_pos;

// 网络杂项数据
typedef struct {
	/* 0:  extra data */
	/* 1:  backfocal_pos */
	uint32_t type;
	uint32_t len;
} NetMiscData;

#define HW_EXTEND_PROTOCOL_SET_VOUT 0xff00102e
#define HW_EXTEND_PROTOCOL_GET_VOUT 0xff00102f
typedef struct {
	uint8_t vout_id;
	uint8_t enable;
	uint8_t reserve[64];
} NetVout;


/* osd theme */
#define HW_EXTEND_PROTOCOL_GET_OSD_THEME 0xff001f00
#define HW_EXTEND_PROTOCOL_SET_OSD_THEME 0xff001f01
typedef struct {
	uint8_t slot;                           /* 通道 */
	uint8_t use_outline;                    /* 是否使用描边 */
	uint8_t gap[2];                         /* 对齐 */
	uint32_t color;                         /* osd颜色, 0:白色 1:黑色 2:蓝色 3:红色 */
	uint32_t font_size;                     /* 字体大小,次码流按比例缩小 */
	uint8_t reserve[64];
} NetOsdTheme;


#if 0
#define HW_EXTEND_PROTOCOL_GET_VIDEO_STANDARD 0xff001032
#define HW_EXTEND_PROTOCOL_SET_VIDEO_STANDARD 0xff001033
typedef struct {
	uint8_t video_standard;	//视频标准. 0:pal 1:ntsc 2:60fps 3:5M
	uint8_t reserve1[3];
	uint32_t vscap_bitset;	//视频标准能力位集
	uint8_t reserve[64];
} NetVideoStandard;
#endif

/* 抓图 */
#define HW_EXTEND_PROTOCOL_STILL_CAPTURE 0xff001035
typedef struct {
	uint8_t slot;    /* 通道 */
	uint8_t stream;  /* 码流 */
	uint8_t quality; /* 1～100 */
	uint8_t type;    /* 0:jpg 1:bmp */
	char filename[64];  /* 文件名,绝对路径 */
	uint8_t reserve[64];
} NetStillCapture;

/*  扩展的手动录像协议 */
#define HW_EXTEND_PROTOCOL_MANUAL_RECORD 0xff001036
typedef struct {
	uint8_t cmd;	// 0:停止 1:启动
	uint8_t slot;
	uint8_t stream;
	uint8_t loop; 	// 0:不覆盖 1:循环覆盖
	uint8_t reserve[64];
} NetManualRecord;

/********* 以下是测试用协议 *************/
/* 测试过滤片 */
#define HW_EXTEND_PROTOCOL_TEST_FILTER	0xff001100 
typedef struct {
	uint8_t mode;	//0: day	1:night
	uint8_t reserve[32];
} net_test_filter_t;

/* 测试后焦电机，来回移动 */
#define HW_EXTEND_PROTOCOL_BACK_FOCAL_TEST 0xff001101 
typedef struct {
	uint8_t enable;	//0: start	1:stop
	uint8_t reserve[32];
} NetBackFocalTest;


#endif

