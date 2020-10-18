package ru.stk.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.stk.common.MsgLib;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClientInHandler extends ChannelInboundHandlerAdapter {
    /*Defines current stage in communication with client */
    public enum State {
        IDLE, NAME_LEN, NAME, FILE_LEN, FILE
    }

    private State curState = State.IDLE;
    private int fileNameLength;
    private long fileLength;
    private long inFileLength;
    private BufferedOutputStream fileSaveStream;
    private byte[] msgBytes = new byte[MsgLib.MSG_LEN];
    private String msgString = "";
    private final String fileFolder = "c_storage";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        ByteBuf buf = ((ByteBuf) msg);
        curState = State.IDLE;

        while (buf.readableBytes() > 0) {
            /* TODO: Here establish call of different commands - Cmd and FileService */

            if (curState == State.IDLE) {
                buf.readBytes(msgBytes);
                msgString = new String(msgBytes, "UTF-8");
            }

            /* Receive command of the incoming new file - FLE means a file */
            if (curState == State.IDLE & msgString.equals("FLE")) {
                curState = State.NAME_LEN;
                inFileLength = 0L;
                System.out.println("STATE: Start file receiving");
            }

            /* Receive length of a new file name */
            if (curState == State.NAME_LEN) {
                if (buf.readableBytes() >= 4) {
                    System.out.println("STATE: Get filename length");
                    fileNameLength = buf.readInt();
                    curState = State.NAME;
                }
            }

            /* Receive file name and create a new file in user's directory */
            if (curState == State.NAME) {
                if (buf.readableBytes() >= fileNameLength) {
                    byte[] fileName = new byte[fileNameLength];
                    buf.readBytes(fileName);
                    System.out.println("STATE: Filename received - __" + new String(fileName, "UTF-8"));
                    fileSaveStream = new BufferedOutputStream
                            (new FileOutputStream("__" + new String(fileName)));
                    curState = State.FILE_LEN;
                }
            }

            /* Receive length of a new file */
            if (curState == State.FILE_LEN) {
                if (buf.readableBytes() >= 8) {
                    fileLength = buf.readLong();
                    System.out.println("STATE: File length received - " + fileLength);
                    curState = State.FILE;
                }
            }

            /*TODO: Put the file to the valid directory of the user */
            /*TODO: Add error messages to the user and log for all blocks */
            /* Receive and save content of the file */
            if (curState == State.FILE) {
                while (buf.readableBytes() > 0) {
                    fileSaveStream.write(buf.readByte());
                    inFileLength++;
                    if (fileLength == inFileLength) {
                        curState = State.IDLE;
                        System.out.println("File received and saved");
                        fileSaveStream.close();
                        break;
                    }
                }
                curState = State.IDLE;
            }

        }

        if (buf.readableBytes() == 0) {
            buf.release();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}