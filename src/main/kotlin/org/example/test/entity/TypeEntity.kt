package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "type")
class TypeEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type", nullable = false)
    val id_type: Long? = null,

    @Column(name = "priority", nullable = false)
    var priority: Long? = null,

    @Column(name = "name")
    var name: String? = null,


) {
}