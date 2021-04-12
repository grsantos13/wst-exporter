package br.com.gn.exporter.register

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "register_code_uk", columnNames = ["code"])
    ]
)
class Register(
    @field:NotBlank @field:Size(max = 8) @Column(nullable = false, unique = true, updatable = false) val code: String,
    @field:NotBlank @Column(nullable = false) val name: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null

}
