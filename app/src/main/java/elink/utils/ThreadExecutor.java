
package elink.utils;


import com.coolkit.common.HLog;

public class ThreadExecutor {
	public static void execute(Runnable task) {
		HLog.i(ThreadExecutor.class.getSimpleName(), "exe task:");
		new Thread(task).start();
	}
	

}
