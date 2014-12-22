#
# Android NDK makefile 
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libiconv
LIBICONV := libiconv

LOCAL_CFLAGS := -I$(LOCAL_PATH)/$(LIBICONV)
LOCAL_SRC_FILES := $(LIBICONV)/iconv.c

include $(BUILD_STATIC_LIBRARY)

# libzbarjni
include $(CLEAR_VARS)

LOCAL_MODULE := zbarjni

LOCAL_SRC_FILES := ./zbar/zbarjni.c \
            ./zbar/img_scanner.c \
		   ./zbar/decoder.c \
		   ./zbar/image.c \
		   ./zbar/symbol.c \
		   ./zbar/convert.c \
		   ./zbar/config.c \
		   ./zbar/scanner.c \
		   ./zbar/error.c \
		   ./zbar/refcnt.c \
		   ./zbar/video.c \
		   ./zbar/video/null.c \
		   ./zbar/decoder/code128.c \
		   ./zbar/decoder/code39.c \
		   ./zbar/decoder/code93.c \
		   ./zbar/decoder/codabar.c \
		   ./zbar/decoder/databar.c \
		   ./zbar/decoder/ean.c \
		   ./zbar/decoder/i25.c \
		   ./zbar/decoder/qr_finder.c \
		   ./zbar/qrcode/bch15_5.c \
		   ./zbar/qrcode/binarize.c \
		   ./zbar/qrcode/isaac.c \
		   ./zbar/qrcode/qrdec.c \
		   ./zbar/qrcode/qrdectxt.c \
		   ./zbar/qrcode/rs.c \
		   ./zbar/qrcode/util.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/zbar \
                    $(LOCAL_PATH)/libiconv

LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := libiconv

include $(BUILD_SHARED_LIBRARY)
