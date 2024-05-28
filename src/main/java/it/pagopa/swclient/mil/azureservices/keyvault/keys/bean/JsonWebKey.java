/*
 * JsonWebKey.java
 *
 * 11 apr 2024
 */
package it.pagopa.swclient.mil.azureservices.keyvault.keys.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArrayDeserializer;
import it.pagopa.swclient.mil.azureservices.keyvault.keys.util.ByteArraySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * As of http://tools.ietf.org/html/draft-ietf-jose-json-web-key-18
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/rest/api/keyvault/keys/create-key/create-key?view=rest-keyvault-keys-7.4&tabs=HTTP#jsonwebkey">Microsoft
 *      Azure Documentation</a>
 * 
 * @author Antonio Tarricone
 */
@RegisterForReflection
@Getter
@Setter
@Accessors(chain = true)
@ToString
@JsonInclude(value = Include.NON_NULL)
public class JsonWebKey {
	/**
	 * JSON keys.
	 */
	public static final String CRV_JK = "crv";
	public static final String D_JK = "d";
	public static final String DP_JK = "dp";
	public static final String DQ_JK = "dq";
	public static final String E_JK = "e";
	public static final String K_JK = "k";
	public static final String KEY_HSM_JK = "key_hsm";
	public static final String KEY_OPS_JK = "key_ops";
	public static final String KID_JK = "kid";
	public static final String KTY_JK = "kty";
	public static final String N_JK = "n";
	public static final String P_JK = "p";
	public static final String Q_JK = "q";
	public static final String QI_JK = "qi";
	public static final String X_JK = "x";
	public static final String Y_JK = "y";

	/**
	 * Elliptic curve name. For valid values, see {@link JsonWebKeyCurveName}
	 */
	@JsonProperty(CRV_JK)
	private String crv;

	/**
	 * RSA private exponent, or the D component of an EC private key.
	 */
	@JsonProperty(D_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] d;

	/**
	 * RSA private key parameter.
	 */
	@JsonProperty(DP_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] dp;

	/**
	 * RSA private key parameter.
	 */
	@JsonProperty(DQ_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] dq;

	/**
	 * RSA public exponent.
	 */
	@JsonProperty(E_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] e;

	/**
	 * Symmetric key.
	 */
	@JsonProperty(K_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] k;

	/**
	 * Protected Key, used with 'Bring Your Own Key'.
	 */
	@JsonProperty(KEY_HSM_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] keyHsm;

	/**
	 * Supported key operations.
	 * 
	 * @see JsonWebKeyOperation
	 */
	@JsonProperty(KEY_OPS_JK)
	private List<String> keyOps;

	/**
	 * Key identifier.
	 */
	@JsonProperty(KID_JK)
	private String kid;

	/**
	 * JsonWebKey Key Type (kty), as defined in
	 * {@link https://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-40}
	 * 
	 * @see JsonWebKeyType
	 */
	@JsonProperty(KTY_JK)
	private String kty;

	/**
	 * RSA modulus.
	 */
	@JsonProperty(N_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] n;

	/**
	 * RSA secret prime.
	 */
	@JsonProperty(P_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] p;

	/**
	 * RSA secret prime, with p < q.
	 */
	@JsonProperty(Q_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] q;

	/**
	 * RSA private key parameter.
	 */
	@JsonProperty(QI_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	@ToString.Exclude
	private byte[] qi;

	/**
	 * X component of an EC public key.
	 */
	@JsonProperty(X_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] x;

	/**
	 * Y component of an EC public key.
	 */
	@JsonProperty(Y_JK)
	@JsonSerialize(using = ByteArraySerializer.class)
	@JsonDeserialize(using = ByteArrayDeserializer.class)
	private byte[] y;
}