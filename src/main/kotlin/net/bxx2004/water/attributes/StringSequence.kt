package net.bxx2004.water.attributes

import net.bxx2004.water.submit

class StringSequence(step:Int,val content :List<String>) :Sequence(-1,-1,step) {
    var index = 0
    var ctime = -1
    override fun next(): Union<Boolean, Any> {
        if (ctime == -1){
            submit(-1,50){task->
                ctime++
                if (ctime >= step.toDouble()){
                    ctime = -1
                    index++
                    task.cancel()
                }
            }
        }
        if (index >= content.size){
            return Union(false,content.last())
        }
        val r = content[index]
        return Union(true,r)
    }

    override fun isEnding(): Boolean {
        return index >= content.size
    }

    override fun reset() {
        index = 0
    }
}