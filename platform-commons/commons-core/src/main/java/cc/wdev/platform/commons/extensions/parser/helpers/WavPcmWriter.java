package cc.wdev.platform.commons.extensions.parser.helpers;

import lombok.Getter;

import java.io.*;
import java.nio.ShortBuffer;

/**
 * 简易 WAV（PCM 16bit）写入器：先写占位头，{@link #finish()} 时回填数据长度。
 *
 * @author elvea
 */
public class WavPcmWriter implements Closeable {

    private static final int WAV_HEADER_SIZE = 44;

    private final File file;

    private final DataOutputStream out;

    private final int sampleRate;

    private final int channels;

    @Getter
    private long dataSize;

    private boolean finished;

    public WavPcmWriter(File file, int sampleRate, int channels) throws IOException {
        this.file = file;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        this.writeHeader();
    }

    /**
     * 写入一帧 PCM 数据（planar 布局，按声道交错输出）
     */
    public void write(ShortBuffer[] samples, int count) throws IOException {
        if (samples == null || samples.length == 0 || count <= 0) {
            return;
        }
        int frameChannels = samples.length;
        for (int i = 0; i < count; i++) {
            for (ShortBuffer sample : samples) {
                this.out.writeShort(Short.reverseBytes(sample.get(i)));
            }
        }
        this.dataSize += (long) count * frameChannels * Short.BYTES;
    }

    /**
     * 收尾：关闭输出流并回填 RIFF / data 长度
     */
    public void finish() throws IOException {
        if (this.finished) {
            return;
        }
        this.out.flush();
        this.out.close();
        try (RandomAccessFile raf = new RandomAccessFile(this.file, "rw")) {
            raf.seek(4);
            raf.writeInt(Integer.reverseBytes((int) (WAV_HEADER_SIZE - 8 + this.dataSize)));
            raf.seek(40);
            raf.writeInt(Integer.reverseBytes((int) this.dataSize));
        }
        this.finished = true;
    }

    @Override
    public void close() throws IOException {
        this.finish();
    }

    private void writeHeader() throws IOException {
        this.out.writeBytes("RIFF");
        this.out.writeInt(0);
        this.out.writeBytes("WAVE");
        this.out.writeBytes("fmt ");
        this.out.writeInt(Integer.reverseBytes(16));
        this.out.writeShort(Short.reverseBytes((short) 1));
        this.out.writeShort(Short.reverseBytes((short) this.channels));
        this.out.writeInt(Integer.reverseBytes(this.sampleRate));
        this.out.writeInt(Integer.reverseBytes(this.sampleRate * this.channels * Short.BYTES));
        this.out.writeShort(Short.reverseBytes((short) (this.channels * Short.BYTES)));
        this.out.writeShort(Short.reverseBytes((short) 16));
        this.out.writeBytes("data");
        this.out.writeInt(0);
    }

}
