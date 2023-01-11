package client;

import com.asset.cs.sendingsms.controller.Controller;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * A class extending the Thread Class its function is to register itself with
 * the JVM and in case of termination (SIGNAL) when the JVM tries to kill the
 * process the functionalitis in the run procedure to be done first before
 * exiting.
 *
 * this mechanism is called (addShutdownHook), for last minute actions before
 * termination
 *
 *****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class ShutdownHook extends Thread {

    Controller controller;

    public ShutdownHook(Controller controller) {
        super("ShutdownHook");
        this.controller = controller;
    }

    public void run() {
        controller.setExitFlag(true);
        controller.WaitNotify();
    }
}
