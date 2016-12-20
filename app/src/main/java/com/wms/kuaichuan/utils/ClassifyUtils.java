package com.wms.kuaichuan.utils;

import com.wms.kuaichuan.core.entity.FileInfo;
import com.wms.kuaichuan.core.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 王梦思 on 2016/12/14.
 * <p/>
 */
public class ClassifyUtils {

    /**
     * 过滤指定类型的FileInfo
     */
    public static List<FileInfo> filter(Map<String, FileInfo> hashMap, int type) {
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();

        for (Map.Entry<String, FileInfo> entry : hashMap.entrySet()) {
            FileInfo fileInfo = entry.getValue();
//            if(FileUtils.isApkFile(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if(FileUtils.isJpgFile(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if (FileUtils.isMp3File(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }else if (FileUtils.isMp4File(fileInfo.getFilePath())){
//                fileInfos.add(fileInfo);
//            }

            switch (type) {
                case FileInfo.TYPE_APK: {
                    if (FileUtils.isApkFile(fileInfo.getFilePath())) {
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_JPG: {
                    if (FileUtils.isJpgFile(fileInfo.getFilePath())) {
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_MP3: {
                    if (FileUtils.isMp3File(fileInfo.getFilePath())) {
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
                case FileInfo.TYPE_MP4: {
                    if (FileUtils.isMp4File(fileInfo.getFilePath())) {
                        fileInfos.add(fileInfo);
                    }
                    break;
                }
            }
        }

        return fileInfos;
    }

}
