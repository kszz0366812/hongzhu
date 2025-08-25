package com.insightflow.common.util;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

/**
 * SFTP 文件传输工具类
 */
@Component
public class SftpUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);
    
    // 远程服务器配置 - 从配置文件读取
    @Value("${sftp.host}")
    private String host;
    
    @Value("${sftp.port}")
    private int port;
    
    @Value("${sftp.username}")
    private String username;
    
    @Value("${sftp.password}")
    private String password;
    
    @Value("${sftp.remote-base-path}")
    private String remoteBasePath;
    
    @Value("${sftp.connection-timeout}")
    private int connectionTimeout;
    
    @Value("${sftp.session-timeout}")
    private int sessionTimeout;
    
    @Value("${sftp.http-base-url}")
    private String httpBaseUrl;
    
    /**
     * 上传文件到远程服务器
     * @param localFilePath 本地文件路径
     * @param remoteFileName 远程文件名
     * @return 是否上传成功
     */
    public boolean uploadFile(String localFilePath, String remoteFileName) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 创建会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            
            // 设置连接参数
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接服务器
            session.connect(connectionTimeout);
            logger.info("SSH 连接成功: {}:{}", host, port);
            
            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(sessionTimeout);
            logger.info("SFTP 通道连接成功");
            
            // 切换到远程目录
            try {
                channelSftp.cd(remoteBasePath);
            } catch (SftpException e) {
                // 如果目录不存在，创建目录
                createRemoteDirectory(channelSftp, remoteBasePath);
                channelSftp.cd(remoteBasePath);
            }
            
            // 上传文件
            channelSftp.put(localFilePath, remoteFileName);
            logger.info("文件上传成功: {} -> {}", localFilePath, remoteFileName);
            
            return true;
            
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            return false;
        } finally {
            // 关闭连接
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 上传文件流到远程服务器
     * @param inputStream 文件输入流
     * @param remoteFileName 远程文件名
     * @return 是否上传成功
     */
    public boolean uploadFileStream(InputStream inputStream, String remoteFileName) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 创建会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            
            // 设置连接参数
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接服务器
            session.connect(connectionTimeout);
            logger.info("SSH 连接成功: {}:{}", host, port);
            
            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(sessionTimeout);
            logger.info("SFTP 通道连接成功");
            
            // 切换到远程目录
            try {
                channelSftp.cd(remoteBasePath);
            } catch (SftpException e) {
                // 如果目录不存在，创建目录
                createRemoteDirectory(channelSftp, remoteBasePath);
                channelSftp.cd(remoteBasePath);
            }
            
            // 上传文件流
            channelSftp.put(inputStream, remoteFileName);
            logger.info("文件流上传成功: {}", remoteFileName);
            
            return true;
            
        } catch (Exception e) {
            logger.error("文件流上传失败: {}", e.getMessage(), e);
            return false;
        } finally {
            // 关闭连接
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 创建远程目录
     * @param channelSftp SFTP 通道
     * @param remotePath 远程路径
     */
    private void createRemoteDirectory(ChannelSftp channelSftp, String remotePath) throws SftpException {
        String[] paths = remotePath.split("/");
        String currentPath = "";
        
        for (String path : paths) {
            if (path.isEmpty()) {
                continue;
            }
            
            currentPath += "/" + path;
            try {
                channelSftp.cd(currentPath);
            } catch (SftpException e) {
                // 目录不存在，创建目录
                channelSftp.mkdir(currentPath);
                channelSftp.cd(currentPath);
                logger.info("创建远程目录: {}", currentPath);
            }
        }
    }
    
    /**
     * 获取远程文件的完整URL
     * @param fileName 文件名
     * @return 完整的URL
     */
    public String getRemoteFileUrl(String fileName) {
        return httpBaseUrl + "/" + fileName;
    }
    
    /**
     * 删除远程文件
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileName,String refType) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 创建会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            
            // 设置连接参数
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接服务器
            session.connect(connectionTimeout);
            logger.info("SSH 连接成功: {}:{}", host, port);
            
            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(sessionTimeout);
            logger.info("SFTP 通道连接成功");
            
            // 切换到远程目录
            try {
                channelSftp.cd(remoteBasePath);
                if(StringUtils.isNotEmpty(refType)){
                    channelSftp.cd(refType);
                }
            } catch (SftpException e) {
                logger.error("远程目录不存在: {}", e.getMessage(), e);
                return false;
            }
            
            // 删除文件
            try {
                channelSftp.rm(fileName);
                logger.info("远程文件删除成功: {}", fileName);
                return true;
            } catch (SftpException e) {
                logger.error("删除远程文件失败: {}, 错误: {}", fileName, e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("删除远程文件异常: {}, 错误: {}", fileName, e.getMessage(), e);
            return false;
        } finally {
            // 关闭连接
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 上传文件到指定文件夹
     * @param localFilePath 本地文件路径
     * @param remoteFileName 远程文件名
     * @param remoteFolderPath 远程文件夹路径
     * @return 是否上传成功
     */
    public boolean uploadFileToFolder(String localFilePath, String remoteFileName, String remoteFolderPath) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // 创建会话
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            
            // 设置连接参数
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接服务器
            session.connect(connectionTimeout);
            logger.info("SSH 连接成功: {}:{}", host, port);
            
            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(sessionTimeout);
            logger.info("SFTP 通道连接成功");
            
            // 切换到远程目录
            try {
                channelSftp.cd(remoteBasePath);
            } catch (SftpException e) {
                // 如果基础目录不存在，创建目录
                createRemoteDirectory(channelSftp, remoteBasePath);
                channelSftp.cd(remoteBasePath);
            }
            
            // 创建并切换到目标文件夹
            try {
                channelSftp.cd(remoteFolderPath);
            } catch (SftpException e) {
                // 如果目标文件夹不存在，创建目录
                createRemoteDirectory(channelSftp, remoteFolderPath);
                channelSftp.cd(remoteFolderPath);
            }
            
            // 上传文件
            channelSftp.put(localFilePath, remoteFileName);
            logger.info("文件上传成功: {} -> {}/{}", localFilePath, remoteFolderPath, remoteFileName);
            
            return true;
            
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            return false;
        } finally {
            // 关闭连接
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    /**
     * 获取远程基础路径
     * @return 远程基础路径
     */
    public String getRemoteBasePath() {
        return remoteBasePath;
    }
    
    /**
     * 获取HTTP基础URL
     * @return HTTP基础URL
     */
    public String getHttpBaseUrl() {
        return httpBaseUrl;
    }
} 