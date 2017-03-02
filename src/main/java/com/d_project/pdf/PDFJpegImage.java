package com.d_project.pdf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * PDFJpegImage
 * @author Kazuhiko Arase
 */
public class PDFJpegImage extends PDFImage {

    public PDFJpegImage(String path) throws IOException {
        this(readFile(path) );
    }

    public PDFJpegImage(byte[] data) throws IOException {

        super(data);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data) );

        try {

            int jpegHeader;   
            int S;

            jpegHeader = in.readShort() & 0xffff;
        
            if (jpegHeader != 0xffD8) {
                throw new IOException("Not JPEG");
            }

            seekMarker(in);

            put("BitsPerComponent", new PDFInteger(in.readByte() & 0xff ) );
            put("Height", new PDFInteger(in.readShort() & 0xffff) );
            put("Width", new PDFInteger(in.readShort() & 0xffff) );

            S = in.readByte() & 0xff;        

            if (S != 3) {
                throw new IOException("Not RGB");
            }

        } finally {
            in.close();
        }

        put("ColorSpace", new PDFName("DeviceRGB") );
        put("Filter", new PDFName("DCTDecode") );
    }

    private static byte[] readFile(String path) throws IOException {

        InputStream in = new BufferedInputStream(new FileInputStream(path) );

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int b;
            while ( (b = in.read() ) != -1) {
                out.write(b);
            }

            out.close();

            return out.toByteArray();

        } finally {
            in.close();
        }
    }

    private void seekMarker(DataInputStream in) throws IOException {

        while (true) {
            
            int marker = in.readShort() & 0xffff;
            int len = in.readShort() & 0xffff;

            switch(marker) {
            case 0xffc0 :
                return;

            default :
                in.skip(len - 2);
                break;
            }
        }
    }
}


