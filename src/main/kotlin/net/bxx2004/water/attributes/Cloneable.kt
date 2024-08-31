package net.bxx2004.water.attributes
import java.io.Serializable

interface Cloneable<T> : Serializable{
    fun clone():T
}