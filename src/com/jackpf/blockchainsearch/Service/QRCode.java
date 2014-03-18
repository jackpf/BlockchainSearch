package com.jackpf.blockchainsearch.Service;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * QR code generator
 */
public class QRCode
{
    /**
     * Create a qrcode bitmap from string
     * 
     * @param qrCodeText
     * @param size
     * @return
     * @throws WriterException
     */
    public static Bitmap create(String qrCodeText, int size)
    {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
            
            int matrixWidth = byteMatrix.getWidth();
            Bitmap image = Bitmap.createBitmap(matrixWidth, matrixWidth, Bitmap.Config.RGB_565);
            
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    int c = Color.rgb(0xFF, 0xFF, 0xFF);
                    if (byteMatrix.get(i, j)) {
                        c = Color.rgb(0x0, 0x0, 0x0);
                    }
                    image.setPixel(i, j, c);
                }
            }
            
            return image;
        } catch (WriterException e) {
            return null;
        }
    }
}
