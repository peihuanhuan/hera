package net.peihuan.hera.component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import net.peihuan.hera.config.security.propertity.JwtProperties
import net.peihuan.hera.constants.JWT_ROLE_FIELD
import org.joda.time.DateTime
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.function.Function

@Component
class JwtTokenComponent(val jwtProperties: JwtProperties) : Serializable {


    fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }


    fun getAllClaimsFromToken(token: String): Claims = Jwts.parser()
        .setSigningKey(jwtProperties.secret)
        .parseClaimsJws(token)
        .body


    private fun generateToken(subject: String, claims: Map<String, Any>, expireMinute: Int): String {
        val now = DateTime.now()
        val expirationDate = now.plusMinutes(expireMinute).toDate()
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now.toDate())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS512, jwtProperties.secret)
            .compact()
    }

    fun generateUserToken(
        userId: Long, vararg roles: String,
        expireMinute: Int = jwtProperties.expireMinute,
    ): String {
        val params = mutableMapOf<String, Any>(
            JWT_ROLE_FIELD to roles.joinToString(","),
        )

        return generateToken(
            userId.toString(),
            params,
            expireMinute
        )
    }

}