package com.xc.sftp;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
* @ClassName: SFTPChannel 
* @Description: TODO
* @author xuechen
* @date 2017年1月11日 下午2:50:51
*  
*/
public class SFTPChannel {

	Session session = null;
    Channel channel = null;
    
    private static final Logger LOG = Logger.getLogger(SFTPChannel.class.getName());
    
    public ChannelSftp getChannel(Map<String, String> sftpDetails, int timeout) throws JSchException {
    	
    	String ftpHost = sftpDetails.get(SFTPConstants.SFTP_REQ_HOST);
        String port = sftpDetails.get(SFTPConstants.SFTP_REQ_PORT);
        String ftpUserName = sftpDetails.get(SFTPConstants.SFTP_REQ_USERNAME);
        String ftpPassword = sftpDetails.get(SFTPConstants.SFTP_REQ_PASSWORD);

        int ftpPort = SFTPConstants.SFTP_DEFAULT_PORT;
        if (port != null && !port.equals("")) {
            ftpPort = Integer.valueOf(port);
        }
        
        JSch jsch = new JSch();
        session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
        LOG.debug("session created");
        if(ftpPassword != null) {
        	session.setPassword(ftpPassword);
        }
        
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(timeout);
        session.connect();
        LOG.debug("session connected");
        
        LOG.debug("Opening Channel");
        channel = session.openChannel("sftp");
        channel.connect();
        LOG.debug("Connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = " + ftpUserName
                + ", returning: " + channel);
    	return (ChannelSftp) channel;
    }
    
    public void closeChannel() {
    	if(channel != null) {
    		channel.disconnect();
    	}
    	if(session != null) {
    		session.disconnect();
    	}
    }
    
}
