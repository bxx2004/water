package net.bxx2004.water.attributes
import net.bxx2004.water.submit
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

class Group<T:Sequence>(val repeat:Boolean = false) : Serializable{
    private var cache_time = -1
    private var cache_index = -1
    private val members = CopyOnWriteArrayList<Triple<String,Int,T>>()
    private lateinit var currentElement:Triple<String,Int,T>
    fun add(id:String,spacing:Int,member:T):Boolean{
        if (members.map { it.left }.contains(id)){
            return false
        }
        members.add(Triple(id,spacing,member))
        return true
    }
    fun remove(id:String):Boolean{
        if (!members.map { it.left }.contains(id)){
            return false
        }
        return members.removeIf { it.left == id }
    }
    fun compute():Union<String,T>{
        if (cache_time == -1){
            cache_index++
            if (cache_index >= members.size){
                if (repeat) {
                    members.forEach {
                        it.right.reset()
                    }
                    cache_index = 0
                }else{
                    return Union(currentElement.left,currentElement.right)
                }
            }
            currentElement = members[cache_index]
            cache_time = 0
            submit(-1,50){task->
                cache_time++
                if (cache_time >= currentElement.middle && currentElement.right.isEnding()){
                    cache_time = -1
                    task.cancel()
                }
            }
        }
        return Union(currentElement.left,currentElement.right)
    }
}