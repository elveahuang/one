package cc.wdev.platform.commons.oapis.facebody;

/**
 * @author elvea
 */
public interface FaceBodyService {

    FaceBodyResult detectFace(String target);

    FaceBodyResult compareFace(String target, String source);

}
