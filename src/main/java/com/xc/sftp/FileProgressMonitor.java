package com.xc.sftp;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.jcraft.jsch.SftpProgressMonitor;

/** 
* @ClassName: FileProgressMonitor 
* @Description: TODO
* @author xuechen
* @date 2017年1月12日 下午4:59:09
*  
*/
public class FileProgressMonitor extends TimerTask implements SftpProgressMonitor {
	
	private long progressInterval = 5 * 1000; // 默认间隔时间为5秒
    
    private boolean isEnd = false; // 记录传输是否结束
    
    private long transfered; // 记录已传输的数据总大小
    
    private long fileSize; // 记录文件总大小
    
    private Timer timer; // 定时器对象
    
    private boolean isScheduled = false; // 记录是否已启动timer记时器

	/**
	 * @param fileSize
	 */
	public FileProgressMonitor(long fileSize) {
		this.fileSize = fileSize;
	}

	/* (non-Javadoc)
	 * @see com.jcraft.jsch.SftpProgressMonitor#count(long)
	 */
	@Override
	public boolean count(long count) {
		if(isEnd) return false;
		if(!isScheduled) {
			start();
		}
		add(count);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.jcraft.jsch.SftpProgressMonitor#end()
	 */
	@Override
	public void end() {
		setEnd(true);

	}

	/* (non-Javadoc)
	 * @see com.jcraft.jsch.SftpProgressMonitor#init(int, java.lang.String, java.lang.String, long)
	 */
	@Override
	public void init(int arg0, String arg1, String arg2, long arg3) {

	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		if (!isEnd()) { // 判断传输是否已结束
			System.out.println("Transfering is in progress.");
            long transfered = getTransfered();
            if (transfered != fileSize) { // 判断当前已传输数据大小是否等于文件总大小
                System.out.println("Current transfered: " + transfered + " bytes");
                sendProgressMessage(transfered);
            } else {
                System.out.println("File transfering is done.");
                setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
            }
        } else {
            System.out.println("Transfering done");
            stop(); // 如果传输结束，停止timer记时器
            return;
        }

	}
	
	public void start() {
		if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(this, 1000, progressInterval);
        isScheduled = true;
	}
	
	private synchronized void add(long count) {
        transfered = transfered + count;
    }
	
	private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
    
    private synchronized boolean isEnd() {
        return isEnd;
    }
    
    private synchronized long getTransfered() {
        return transfered;
    }
    
    /**
     * 打印progress信息
     * @param transfered
     */
    private void sendProgressMessage(long transfered) {
        if (fileSize != 0) {
            double d = ((double)transfered * 100)/(double)fileSize;
            DecimalFormat df = new DecimalFormat( "#.##"); 
            System.out.println("Sending progress message: " + df.format(d) + "%");
        } else {
            System.out.println("Sending progress message: " + transfered);
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isScheduled = false;
        }
    }

}
