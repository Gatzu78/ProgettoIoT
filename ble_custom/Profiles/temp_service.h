/******************************************************************************

   @file  temp_service.h

   @brief   This file contains the Temp_Service service definitions and
          prototypes.


   Group: WCS, BTS
   Target Device: cc13x2_26x2

 ******************************************************************************

 *****************************************************************************/

#ifndef _TEMP_SERVICE_H_
#define _TEMP_SERVICE_H_

#ifdef __cplusplus
extern "C"
{
#endif

/*********************************************************************
 * INCLUDES
 */
#include <bcomdef.h>
#include <ti/drivers/Timer.h>

/*********************************************************************
 * CONSTANTS
 */
// Service UUID
#define TEMP_SERVICE_SERV_UUID 0x1140
#define TEMP_SERVICE_SERV_UUID_BASE128(uuid) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, \
    0x00, 0xB0, 0x00, 0x40, 0x51, 0x04, LO_UINT16(uuid), HI_UINT16(uuid), 0x00, \
    0xF0

// Temp Characteristic defines
#define TS_TEMP_ID                 0
#define TS_TEMP_UUID               0x1141
#define TS_TEMP_UUID_BASE128(uuid) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, \
    0xB0, 0x00, 0x40, 0x51, 0x04, LO_UINT16(uuid), HI_UINT16(uuid), 0x00, 0xF0
#define TS_TEMP_LEN                4
#define TS_TEMP_LEN_MIN            4

// Sample Characteristic defines
#define TS_SAMPLE_ID                 1
#define TS_SAMPLE_UUID               0x1142
#define TS_SAMPLE_UUID_BASE128(uuid) 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, \
    0xB0, 0x00, 0x40, 0x51, 0x04, LO_UINT16(uuid), HI_UINT16(uuid), 0x00, 0xF0
#define TS_SAMPLE_LEN                1
#define TS_SAMPLE_LEN_MIN            1

// SUPSI Timer0 period for temperature sampling
// callback ogni 250 ms di default (TODO: oltre 300K fallisce)
#define TIMER0_CB_PERIOD                      250000

/*********************************************************************
 * TYPEDEFS
 */

/*********************************************************************
 * MACROS
 */

/*********************************************************************
 * Profile Callbacks
 */

// Callback when a characteristic value has changed
typedef void (*TempServiceChange_t)(uint16_t connHandle, uint8_t paramID,
                                   uint16_t len, uint8_t *pValue);

typedef struct
{
    TempServiceChange_t pfnChangeCb;          // CalTemp when characteristic value changes
    TempServiceChange_t pfnCfgChangeCb;       // CalTemp when characteristic CCCD changes
} TempServiceCBs_t;

/*********************************************************************
 * API FUNCTIONS
 */


/*
 * TempService_AddService- Initializes the TempService service by registering
 *          GATT attributes with the GATT server.
 *
 *    rspTaskId - The ICall Task Id that should receive responses for Indications.
 */
extern bStatus_t TempService_AddService(uint8_t rspTaskId);

/*
 * TempService_RegisterAppCBs - Registers the application callback function.
 *                    Only call this function once.
 *
 *    appCallbacks - pointer to application callbacks.
 */
extern bStatus_t TempService_RegisterAppCBs(TempServiceCBs_t *appCallbacks);

/*
 * TempService_SetParameter - Set a TempService parameter.
 *
 *    param - Profile parameter ID
 *    len   - length of data to write
 *    value - pointer to data to write.  This is dependent on
 *            the parameter ID and may be cast to the appropriate
 *            data type (example: data type of uint16_t will be cast to
 *            uint16_t pointer).
 */
extern bStatus_t TempService_SetParameter(uint8_t param,
                                         uint16_t len,
                                         void *value);

/*
 * TempService_GetParameter - Get a TempService parameter.
 *
 *    param - Profile parameter ID
 *    len   - pointer to a variable that contains the maximum length that can be written to *value.
              After the call, this value will contain the actual returned length.
 *    value - pointer to data to write.  This is dependent on
 *            the parameter ID and may be cast to the appropriate
 *            data type (example: data type of uint16_t will be cast to
 *            uint16_t pointer).
 */
extern bStatus_t TempService_GetParameter(uint8_t param,
                                         uint16_t *len,
                                         void *value);

/*********************************************************************
*********************************************************************/

#ifdef __cplusplus
}
#endif

#endif /* _TEMP_SERVICE_H_ */
