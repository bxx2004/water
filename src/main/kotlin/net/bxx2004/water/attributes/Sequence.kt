package net.bxx2004.water.attributes

import java.io.Serializable

open class Sequence(val start:Number, val end:Number, val step:Number) : Serializable{
    var cacheValue:Double = start.toDouble()
    open fun reset(){
        cacheValue = start.toDouble()
    }
    open fun next():Union<Boolean,Any>{
        cacheValue += step.toDouble()
        if (isEnding()){
            return Union(false,end.toDouble())
        }
        return Union(true,cacheValue)
    }

    open fun isEnding():Boolean{
        if (step.toDouble() < 0){
            return cacheValue <= end.toDouble()
        }else{
            return cacheValue >= end.toDouble()
        }
    }
}