package cc.wdev.platform.commons.extensions.parser.helpers;

import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.leptonica;
import org.bytedeco.tesseract.TessBaseAPI;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tesseract OCR 实现。
 * <p>
 * TessBaseAPI 初始化（加载 tessdata）开销较大且非线程安全，这里按「数据目录 + 语言」维度
 * 维护实例池，减少重复初始化开销；同时保证原生资源（PIX / BytePointer / TessBaseAPI）在
 * 异常路径下也能释放。
 *
 * @author elvea
 */
@Slf4j
public class TesseractHelper {

    /**
     * 池最大实例数
     */
    private static final int POOL_MAX_TOTAL = 8;

    /**
     * 池最大空闲实例数
     */
    private static final int POOL_MAX_IDLE = 2;

    /**
     * 借出实例的最大等待时间
     */
    private static final Duration POOL_BORROW_WAIT = Duration.ofSeconds(10);

    private final ParseConfig config;

    private final ConcurrentMap<String, GenericObjectPool<TessBaseAPI>> pools = new ConcurrentHashMap<>();

    public TesseractHelper(ParseConfig config) {
        this.config = config;
    }

    public String parse(File file) {
        TessBaseAPI api = null;
        boolean invalidated = false;
        PIX pix = null;
        BytePointer outText = null;
        try {
            api = this.borrowApi();
            pix = leptonica.pixRead(file.getAbsolutePath());
            if (pix == null || pix.isNull()) {
                throw new IllegalStateException("failed to read image: " + file.getAbsolutePath());
            }
            api.SetImage(pix);
            outText = api.GetUTF8Text();
            return outText != null && !outText.isNull() ? outText.getString() : "";
        } catch (Exception e) {
            log.error("Tesseract OCR error: {}", e.getMessage(), e);
            if (api != null) {
                this.invalidateApi(api);
                api = null;
                invalidated = true;
            }
            return "";
        } finally {
            if (outText != null && !outText.isNull()) {
                outText.deallocate();
            }
            if (pix != null && !pix.isNull()) {
                leptonica.pixDestroy(pix);
            }
            if (api != null && !invalidated) {
                this.returnApi(api);
            }
        }
    }

    /**
     * 借出一个 Tesseract 实例
     */
    private TessBaseAPI borrowApi() {
        try {
            return this.getPool().borrowObject();
        } catch (Exception e) {
            throw new IllegalStateException("borrow tesseract api failed", e);
        }
    }

    /**
     * 归还实例，归还前清理本次识别的图片状态
     */
    private void returnApi(TessBaseAPI api) {
        try {
            api.Clear();
            this.getPool().returnObject(api);
        } catch (Exception e) {
            log.warn("return tesseract api failed, will destroy: {}", e.getMessage());
            this.invalidateApi(api);
        }
    }

    /**
     * 实例异常后失效，避免脏状态被复用
     */
    private void invalidateApi(TessBaseAPI api) {
        try {
            this.getPool().invalidateObject(api);
        } catch (Exception e) {
            log.warn("invalidate tesseract api failed: {}", e.getMessage());
            api.close();
        }
    }

    /**
     * 获取（或创建）当前配置对应的实例池
     */
    private GenericObjectPool<TessBaseAPI> getPool() {
        String data = this.config.getTesseract().getData();
        String language = this.config.getTesseract().getLanguage();
        String key = data + "|" + language;
        return this.pools.computeIfAbsent(key, k -> new GenericObjectPool<>(
            new TessBaseApiFactory(data, language),
            buildPoolConfig()
        ));
    }

    private static GenericObjectPoolConfig<TessBaseAPI> buildPoolConfig() {
        GenericObjectPoolConfig<TessBaseAPI> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(POOL_MAX_TOTAL);
        poolConfig.setMaxIdle(POOL_MAX_IDLE);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWait(POOL_BORROW_WAIT);
        return poolConfig;
    }

    /**
     * TessBaseAPI 实例工厂：创建时初始化（加载 tessdata），销毁时释放原生资源
     */
    private static final class TessBaseApiFactory extends BasePooledObjectFactory<TessBaseAPI> {

        private final String data;

        private final String language;

        private TessBaseApiFactory(String data, String language) {
            this.data = data;
            this.language = language;
        }

        @Override
        public TessBaseAPI create() throws Exception {
            TessBaseAPI api = new TessBaseAPI();
            if (api.Init(this.data, this.language) != 0) {
                api.close();
                throw new IllegalStateException(
                    "failed to init tesseract: data=" + this.data + ", language=" + this.language);
            }
            return api;
        }

        @Override
        public PooledObject<TessBaseAPI> wrap(TessBaseAPI api) {
            return new DefaultPooledObject<>(api);
        }

        @Override
        public void destroyObject(PooledObject<TessBaseAPI> pooledObject) {
            TessBaseAPI api = pooledObject.getObject();
            try {
                api.End();
            } catch (Exception e) {
                log.warn("tesseract api end failed: {}", e.getMessage());
            }
            api.close();
        }

    }

}
