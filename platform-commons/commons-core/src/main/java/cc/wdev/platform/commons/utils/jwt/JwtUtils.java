package cc.wdev.platform.commons.utils.jwt;

import cc.wdev.platform.commons.utils.EncryptUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JwtUtils
 * 基于Nimbus JOSE + JWT实现的方法
 * <a href="https://connect2id.com/products/nimbus-jose-jwt">...</a>
 *
 * @author elvea
 */
public abstract class JwtUtils {

    public final static JWSAlgorithm DEFAULT_JWT_ALGORITHM = JWSAlgorithm.HS256;

    public final static JWSHeader DEFAULT_JWS_HEADER = new JWSHeader(DEFAULT_JWT_ALGORITHM);

    private static final JWSSignerFactory DEFAULT_JWS_SIGNER_FACTORY = new DefaultJWSSignerFactory();

    public final static String DEFAULT_JWT_SECRET = "TTaVQKspIXJqOsmvIbNCGpPgGNqEjphh";

    public final static byte[] DEFAULT_JWT_SECRET_KEY = DEFAULT_JWT_SECRET.getBytes();

    // -----------------------------------------------------------------------------------------------------------------
    // 基础
    // -----------------------------------------------------------------------------------------------------------------

    public static RSAKey generateRsaKey() throws Exception {
        KeyPair keyPair = EncryptUtils.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    public static JWKSource<SecurityContext> generateJWKSource() throws Exception {
        JWKSet jwkSet = new JWKSet(generateRsaKey());
        return generateJWKSource(jwkSet);
    }

    public static JWKSource<SecurityContext> generateJWKSource(final JWKSet jwkSet) {
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    public static JWKSet createJwkSet() throws Exception {
        return new JWKSet(generateRsaKey());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // JWS
    // -----------------------------------------------------------------------------------------------------------------

    public static String createJwsToken(byte[] secretKey, Map<String, Object> payloadMap) throws JOSEException {
        JWSHeader header = new JWSHeader(DEFAULT_JWT_ALGORITHM);
        JWSSigner signer = new MACSigner(secretKey);
        Payload payload = new Payload(GsonUtils.toJson(payloadMap));
        JWSObject object = new JWSObject(header, payload);
        object.sign(signer);
        return object.serialize();
    }

    public static String createJwsToken(byte[] secretKey, JWTClaimsSet claimsSet) throws JOSEException {
        return createJwsToken(secretKey, claimsSet.toJSONObject());
    }

    public static String createJwsToken(JWTClaimsSet claimsSet) throws JOSEException {
        return createJwsToken(DEFAULT_JWT_SECRET_KEY, claimsSet);
    }

    public static String createJwsToken(Map<String, Object> payloadMap) throws JOSEException {
        return createJwsToken(DEFAULT_JWT_SECRET_KEY, payloadMap);
    }

    public static JWTClaimsSet parseJwsToken(byte[] secretKey, String token) throws ParseException, JOSEException, BadJOSEException {
        ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> source = new ImmutableSecret<>(secretKey);
        JWSKeySelector<SecurityContext> selector = new JWSVerificationKeySelector<>(DEFAULT_JWT_ALGORITHM, source);
        processor.setJWSKeySelector(selector);
        SecurityContext context = new SimpleSecurityContext();
        return processor.process(token, context);
    }

    public static JWTClaimsSet parseJwsToken(String token) throws ParseException, JOSEException, BadJOSEException {
        return parseJwsToken(DEFAULT_JWT_SECRET_KEY, token);
    }

    public static String getJwtId(byte[] secretKey, String token) throws ParseException, JOSEException, BadJOSEException {
        JWTClaimsSet claimsSet = parseJwsToken(secretKey, token);
        return claimsSet.getJWTID();
    }

    public static String getJwtId(String token) throws ParseException, JOSEException, BadJOSEException {
        return getJwtId(DEFAULT_JWT_SECRET_KEY, token);
    }

    public static Map<String, Object> verifyJwsToken(byte[] secretKey, String token) throws ParseException, JOSEException {
        JWSObject object = JWSObject.parse(token);
        Payload payload = object.getPayload();
        JWSVerifier verifier = new MACVerifier(secretKey);
        if (object.verify(verifier)) {
            return payload.toJSONObject();
        }
        return new HashMap<>();
    }

    public static Map<String, Object> verifyJwsToken(String token) throws ParseException, JOSEException {
        return verifyJwsToken(DEFAULT_JWT_SECRET_KEY, token);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // JWT
    // -----------------------------------------------------------------------------------------------------------------

    public static String createJwtToken(byte[] secretKey, JWTClaimsSet claimsSet) throws JOSEException {
        JWSHeader header = new JWSHeader(DEFAULT_JWT_ALGORITHM);
        JWSSigner signer = new MACSigner(secretKey);
        SignedJWT jwt = new SignedJWT(header, claimsSet);
        jwt.sign(signer);
        return jwt.serialize();
    }

    public static String createJwtToken(JWTClaimsSet claimsSet) throws JOSEException {
        return createJwtToken(DEFAULT_JWT_SECRET_KEY, claimsSet);
    }

    public static JWTClaimsSet verifyJwtToken(byte[] secretKey, String token) throws ParseException, JOSEException {
        SignedJWT jwt = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secretKey);
        if (jwt.verify(verifier)) {
            return jwt.getJWTClaimsSet();
        }
        return null;
    }

    public static JWTClaimsSet verifyJwtToken(String token) throws ParseException, JOSEException {
        return verifyJwtToken(DEFAULT_JWT_SECRET_KEY, token);
    }

}
