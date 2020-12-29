/******************************************************************************

   @file  main.c

   @brief main entry of the BLE stack sample application.

   Group: WCS, BTS
   Target Device: cc13x2_26x2

 ******************************************************************************
   
 Copyright (c) 2013-2020, Texas Instruments Incorporated
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 *  Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

 *  Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

 *  Neither the name of Texas Instruments Incorporated nor the names of
    its contributors may be used to endorse or promote products derived
    from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 ******************************************************************************
   
   
 *****************************************************************************/

/*******************************************************************************
 * INCLUDES
 */
#include <stdint.h>

#include <xdc/runtime/Error.h>

#include <ti/sysbios/knl/Clock.h>
#include <ti/drivers/Power.h>
#include <ti/drivers/power/PowerCC26XX.h>
#include <ti/sysbios/BIOS.h>
#include <ti/drivers/UART.h>
#include <ti/drivers/Timer.h>
#include <ti/drivers/PWM.h>
#include <profiles/temp_service.h>

// Comment this in to use xdc.runtime.Log, but also remove UartLog_init below.
//#include <xdc/runtime/Log.h>
#include <ti/common/cc26xx/uartlog/UartLog.h> // Comment out to use xdc Log.

#include <icall.h>
#include "hal_assert.h"
#include "bcomdef.h"
#include "project_zero.h"
#include "math.h"

#ifndef USE_DEFAULT_USER_CFG
#include "ble_user_config.h"
// BLE user defined configuration
icall_userCfg_t user0Cfg = BLE_USER_CFG;
#endif // USE_DEFAULT_USER_CFG

/*******************************************************************************
 * MACROS
 */
#ifndef PI
#define PI 3.14159265
#endif
/*******************************************************************************
 * CONSTANTS
 */

/*******************************************************************************
 * TYPEDEFS
 */

/*******************************************************************************
 * LOCAL VARIABLES
 */

/*******************************************************************************
 * GLOBAL VARIABLES*/
 Timer_Handle    handle[2];


/*******************************************************************************
 * EXTERNS
 */

extern void AssertHandler(uint8_t assertCause,
                          uint8_t assertSubcause);

void TimerCfg(void);
void PWMCfg(void);

/*This Function simulates a temperature variation on a sinus 0.1Hz 20° offset and 5° Vpeak
 * Updates temperature value in temp_tervice*/

void TempSimulator(Timer_Handle handlecaller, int_fast16_t status){
    int temp=20;

    temp += (int)sin(2 * PI * 0.1 * Timer_getCount(handle[1]) / 1000);
    TempService_SetParameter(TS_TEMP_ID, sizeof(temp), &temp);
}



/*******************************************************************************
 * @fn          Main
 *
 * @brief       Application Main
 *
 * input parameters
 *
 * @param       None.
 *
 * output parameters
 *
 * @param       None.
 *
 * @return      None.
 */
int main()
{
  /* Register Application callback to trap asserts raised in the Stack */
  RegisterAssertCback(AssertHandler);

  Board_initGeneral();



#if !defined( POWER_SAVING )
  /* Set constraints for Standby, powerdown and idle mode */
  // PowerCC26XX_SB_DISALLOW may be redundant
  Power_setConstraint(PowerCC26XX_SB_DISALLOW);
  Power_setConstraint(PowerCC26XX_IDLE_PD_DISALLOW);
#endif // POWER_SAVING

    /* Update User Configuration of the stack */
    user0Cfg.appServiceInfo->timerTickPeriod = Clock_tickPeriod;
    user0Cfg.appServiceInfo->timerMaxMillisecond = ICall_getMaxMSecs();

    /* Initialize the RTOS Log formatting and output to UART in Idle thread.
     * Note: Define xdc_runtime_Log_DISABLE_ALL and remove define UARTLOG_ENABLE
     *       to remove all impact of Log statements.
     * Note: NULL as Params gives 115200,8,N,1 and Blocking mode */
    UART_init();
    UartLog_init(UART_open(CONFIG_DISPLAY_UART, NULL));

    /* Initialize ICall module */
    ICall_init();

    /* Start tasks of external images - Priority 5 */
    ICall_createRemoteTasks();

    ProjectZero_createTask();

    /* enable interrupts and start SYS/BIOS */
    BIOS_start();

    Timer_init();
    PWM_init();
    TimerCfg();
    PWMCfg();


    //PIN_setOutputValue(ledPinHandle, CONFIG_PIN_RLED, pCharData->data[0]); //funzione esempio per settare led0

    /** @brief Control output value for GPIO pin
     *
     *  @param handle  Handle provided by previous call to PIN_open()
     *  @param pinId   Pin ID
     *  @param val     Output value (0/1)
     *  @return  #PIN_SUCCESS if successful, else error code
     *  @remark  This function typically has an inlined sibling function in the
     *           device-specific driver that may be used for higher efficiency
     *  @par Usage
     *       @code
     *       PIN_setOutputValue(hPins, PIN_ID(4), 1);
     *       @endcode
     */
    //extern PIN_Status PIN_setOutputValue(PIN_Handle handle, PIN_Id pinId, uint32_t val);

    return(0);
}

/*******************************************************************************
 * @fn          AssertHandler
 *
 * @brief       This is the Application's callback handler for asserts raised
 *              in the stack.  When EXT_HAL_ASSERT is defined in the Stack Wrapper
 *              project this function will be called when an assert is raised,
 *              and can be used to observe or trap a violation from expected
 *              behavior.
 *
 *              As an example, for Heap allocation failures the Stack will raise
 *              HAL_ASSERT_CAUSE_OUT_OF_MEMORY as the assertCause and
 *              HAL_ASSERT_SUBCAUSE_NONE as the assertSubcause.  An application
 *              developer could trap any malloc failure on the stack by calling
 *              HAL_ASSERT_SPINLOCK under the matching case.
 *
 *              An application developer is encouraged to extend this function
 *              for use by their own application.  To do this, add hal_assert.c
 *              to your project workspace, the path to hal_assert.h (this can
 *              be found on the stack side). Asserts are raised by including
 *              hal_assert.h and using macro HAL_ASSERT(cause) to raise an
 *              assert with argument assertCause.  the assertSubcause may be
 *              optionally set by macro HAL_ASSERT_SET_SUBCAUSE(subCause) prior
 *              to asserting the cause it describes. More information is
 *              available in hal_assert.h.
 *
 * input parameters
 *
 * @param       assertCause    - Assert cause as defined in hal_assert.h.
 * @param       assertSubcause - Optional assert subcause (see hal_assert.h).
 *
 * output parameters
 *
 * @param       None.
 *
 * @return      None.
 */
void AssertHandler(uint8_t assertCause, uint8_t assertSubcause)
{
    Log_error2(">>>STACK ASSERT Cause 0x%02x subCause 0x%02x",
               assertCause, assertSubcause);

    // check the assert cause
    switch(assertCause)
    {
    case HAL_ASSERT_CAUSE_OUT_OF_MEMORY:
        Log_error0("***ERROR***");
        Log_error0(">> OUT OF MEMORY!");
        break;

    case HAL_ASSERT_CAUSE_INTERNAL_ERROR:
        // check the subcause
        if(assertSubcause == HAL_ASSERT_SUBCAUSE_FW_INERNAL_ERROR)
        {
            Log_error0("***ERROR***");
            Log_error0(">> INTERNAL FW ERROR!");
        }
        else
        {
            Log_error0("***ERROR***");
            Log_error0(">> INTERNAL ERROR!");
        }
        break;

    case HAL_ASSERT_CAUSE_ICALL_ABORT:
        Log_error0("***ERROR***");
        Log_error0(">> ICALL ABORT!");
        //HAL_ASSERT_SPINLOCK;
        break;

    case HAL_ASSERT_CAUSE_ICALL_TIMEOUT:
        Log_error0("***ERROR***");
        Log_error0(">> ICALL TIMEOUT!");
        //HAL_ASSERT_SPINLOCK;
        break;

    case HAL_ASSERT_CAUSE_WRONG_API_CALL:
        Log_error0("***ERROR***");
        Log_error0(">> WRONG API CALL!");
        //HAL_ASSERT_SPINLOCK;
        break;

    default:
        Log_error0("***ERROR***");
        Log_error0(">> DEFAULT SPINLOCK!");
        //HAL_ASSERT_SPINLOCK;
    }

    return;
}

/*******************************************************************************
 */

void TimerCfg(){
    extern Timer_Handle    handle[2];
    Timer_Params    params;

    Timer_Params_init(&params);
    params.periodUnits = Timer_PERIOD_US;
    params.period = 2000000;                //callback ogni 2 secondi di default
    params.timerMode  = Timer_CONTINUOUS_CALLBACK;
    params.timerCallback = TempSimulator;
    handle[0] = Timer_open(CONFIG_TIMER0, &params);
    Timer_start(handle[0]);

    Timer_Params_init(&params);
    params.periodUnits = Timer_PERIOD_HZ;
    params.period = 1000;
    params.timerMode  = Timer_FREE_RUNNING;
    handle[1] = Timer_open(CONFIG_TIMER1, &params);
    Timer_start(handle[1]);

    //sleep(10000);
}

void PWMCfg(){
    // Import PWM Driver definitions

    PWM_Handle pwm;
    PWM_Params pwmParams;
    uint32_t   dutyValue;
    // Initialize the PWM driver.
    PWM_init();
    // Initialize the PWM parameters
    PWM_Params_init(&pwmParams);
    pwmParams.idleLevel = PWM_IDLE_LOW;      // Output low when PWM is not running
    pwmParams.periodUnits = PWM_PERIOD_HZ;   // Period is in Hz
    pwmParams.periodValue = 1;               // 1 Hz
    pwmParams.dutyUnits = PWM_DUTY_FRACTION; // Duty is in fractional percentage
    pwmParams.dutyValue = 0;                 // 0% initial duty cycle
    // Open the PWM instance
    pwm = PWM_open(CONFIG_PWM0, &pwmParams);

    PWM_start(pwm);                          // start PWM with 5% duty cycle
    dutyValue = (uint32_t) (((uint64_t) PWM_DUTY_FRACTION_MAX * 5) / 100);
    PWM_setDuty(pwm, dutyValue);  // set duty cycle to 5%
}
