package com.wybusy;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        EasyFile.unzip("/qs-server-1.0-SNAPSHOT.zip", "/unzip");
//        EasyFile.unzip("/初中学业考试.zip", "/unzip");
        try {
        EasyFile.zip("/初中学业考试", "/unzip.zip");
        } catch (Exception e){
            System.out.println(e);
        }

        System.out.println( "Hello EasyFile!" );
    }
}
