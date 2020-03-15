package com.sdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author penghuailiang
 * native helper for unity3d
 */
public class NativeHelper
{

    private static Activity gameActivity = null;
    private static Context gameContext = null;
    private final static String TAG = "XNativeHelper";

    public static native void SetAssetManager(AssetManager assetManager);


    public static void setGameActivity(Activity activity, Context context)
    {
        gameActivity = activity;
        gameContext = context;
        SetAssetManager(context.getAssets());
    }

    //获取内存
    public static float GetMemory()
    {
        float memory = -1;
        try
        {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) gameActivity.getSystemService(Context.ACTIVITY_SERVICE);
            Debug.MemoryInfo[] memoryInfoArray = mActivityManager.getProcessMemoryInfo(new int[]{pid});
            memory = (float) memoryInfoArray[0].getTotalPrivateDirty() / 1024;
        }
        catch (Exception e)
        {
            MLog.e(TAG, e.toString());
        }
        return memory;
    }


    /*
     返回值为0时正常 其余都不正常
     */
    public static int CopyStreamingAsset(String src, String dst)
    {
        File dir = new File(dst);
        if (!dir.exists() || !dir.isDirectory())
        {
            dir.mkdir();
        }
        File file = new File(dir, src);
        try
        {
            if (gameContext == null)
            {
                MLog.e(TAG, "CONTEXT IS NULL");
                return -1;
            }
            InputStream is = gameContext.getAssets().open(src);
            if (!is.markSupported())
            {
                MLog.e(TAG, "Input is NULL");
                return -2;
            }
            FileOutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        }
        catch (Exception e)
        {
            MLog.e(TAG, e.getMessage());
            return -3;
        }
        return 0;
    }


    public static byte[] LoadStream(String path)
    {
        InputStream instream = null;
        ByteArrayOutputStream outStream = null;
        try
        {
            instream = gameContext.getAssets().open(path);
            outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[4 * 1024];
            int len;
            while ((len = instream.read(bytes)) != -1)
            {
                outStream.write(bytes, 0, len);
            }
            return outStream.toByteArray();
        }
        catch (IOException e)
        {
            MLog.e(TAG, e.getMessage());
        }
        finally
        {
            try
            {
                if (instream != null)
                {
                    instream.close();
                }
                if (outStream != null)
                {
                    outStream.close();
                }
            }
            catch (Exception e)
            {
                MLog.e(TAG, e.getMessage());
            }
            System.gc();
        }
        return null;
    }

    public static String LoadSteamString(String path)
    {
        InputStream ins = null;
        try
        {
            ins = gameContext.getAssets().open(path);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int idx = -1;
            while ((idx = ins.read()) != -1)
            {
                os.write(idx);
            }
            return os.toString();
        }
        catch (Exception e)
        {
            MLog.e(TAG, e.getMessage());
        }
        finally
        {
            try
            {
                if (ins != null)
                {
                    ins.close();
                }
            }
            catch (Exception e)
            {
                MLog.e(TAG, e.getMessage());
            }
        }
        return "";
    }

    public static void UnZip(final String asset, final String output, final boolean isReWrite)
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    UnZipAssets(asset, output, isReWrite);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void UnZipAssets(String assetName,
                                    String outputDirectory, boolean isReWrite) throws IOException
    {
        //创建解压目标目录
        File file = new File(outputDirectory);
        //如果目标目录不存在，则创建
        if (!file.exists())
        {
            file.mkdirs();
        }
        InputStream inputStream = gameContext.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        //使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        //解压时字节计数
        int count = 0;
        //如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null)
        {
            if (zipEntry.isDirectory())
            {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                //文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists())
                {
                    file.mkdir();
                }
            }
            else
            {
                MLog.d(TAG, zipEntry.getName());
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                //文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists())
                {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0)
                    {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        inputStream.close();
    }
}
