package com.xc.sftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/** 
* @ClassName: SFtpGet 
* @Description: TODO
* @author xuechen
* @date 2017年1月11日 下午2:48:31
*  
*/
public class SFtpGet {
	

	public SFTPChannel getSFTPChannle() {
		return new SFTPChannel();
	}
	
	public static void main(String[] args) throws JSchException {
		sftpGet();
	}
	
	@SuppressWarnings("unchecked")
	public static void sftpGet() throws JSchException {
		SFtpGet sFtpGet = new SFtpGet();

        Map<String, String> sftpDetails = new HashMap<String, String>();
        // 设置主机ip，端口，用户名，密码
        sftpDetails.put(SFTPConstants.SFTP_REQ_HOST, "120.55.96.222");
        sftpDetails.put(SFTPConstants.SFTP_REQ_USERNAME, "tomcat");
        sftpDetails.put(SFTPConstants.SFTP_REQ_PASSWORD, "222yaxin520");
        sftpDetails.put(SFTPConstants.SFTP_REQ_PORT, "22");
        
        SFTPChannel channel = sFtpGet.getSFTPChannle();
        ChannelSftp chSftp = channel.getChannel(sftpDetails, 60000);
        
        String path = "/home/tomcat/xuechen/";
        String filePattern = "*.txt";
        String localPath = "D:\\";
        Set<String> fileSet = new HashSet<>();
        
        while(true) {
        	try {
    			
    			chSftp.cd(path);
    			Vector<LsEntry> files = chSftp.ls(filePattern);
    			for(int i=0; i<files.size(); i++) {
    	        	LsEntry entry = files.get(i);
    	        	String fileName = entry.getFilename();
    	        	if(!fileSet.contains(fileName)) {
    	        		System.out.println("Transferring new file:" + fileName);
    	        		chSftp.get(path + fileName, localPath + fileName, new FileProgressMonitor(entry.getAttrs().getSize()));
    	        		fileSet.add(fileName);
    	        	}
    	        }
    			
    			Thread.sleep(10000);
    		} catch (SftpException e) {
    			e.printStackTrace();
    		} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
