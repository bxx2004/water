package net.bxx2004.water.attributes

// -a @eval[] -b 1 --a --b --c
class Arguments(val content:String) {
    var namespace:String? = null
    val data:HashMap<String,String> = HashMap()
    val tags:List<String> = ArrayList()
    init {
        val c = content.trimStart()
        if (c.startsWith("-")){
            namespace = null
        }else{
            namespace = c.split(" ")[0]
        }
    }
}