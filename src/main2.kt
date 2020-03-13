import java.io.File
import kotlin.math.roundToInt
import kotlin.random.Random

fun main(){

    val train: MutableList<DoubleArray> = mutableListOf();
    File("training.data").forEachLine {
        if (it.isNotEmpty()){
            val tmp = it.split(",")
            train.add(DoubleArray(tmp.size))
            for (i in tmp.indices)
                train.last()[i] = tmp[i].toDouble()
        }
    }

    //****************** ADALINA ******************
    val umbral = 0.0001
    val precision = 0.0
    var epoca = 0

    val w = DoubleArray(train.first().size-1)
    val rnd = Random(1)
    //Pesos aleatorios
    for (i in w.indices)
        w[i] = rnd.nextDouble()

    var EQMAnterior: Double
    var EQMAtual: Double

    do {

        EQMAnterior = ErrorCuadratico(train, w)

        for (i in train.indices){
            var u = 0.0
            for (j in w.indices){
                u += train[i][j] *  w[j]
            }

            for (j in w.indices){
                w[j] = w[j] + umbral * (train[i].last()-u) * train[i][j]
            }
        }

        epoca++

        EQMAtual = ErrorCuadratico(train, w)

    }while (Math.abs(EQMAtual - EQMAnterior) > precision)

    println("Epoca: $epoca")
    for (i in w.indices)
        println("w$i: ${w[i]}")
    println("----------------------------")


    //****************** TEST ******************
    val test: MutableList<DoubleArray> = mutableListOf();
    File("test.data").forEachLine {
        if (it.isNotEmpty()){
            val tmp = it.split(",")
            test.add(DoubleArray(tmp.size))
            for (i in tmp.indices)
                test.last()[i] = tmp[i].toDouble()
        }
    }

    var contador = 0
    var resp: Double
    for (item in test){
        var u = 0.0
        for (i in w.indices){
            u += item[i] * w[i]
        }
        //resp = if (u >= 0) 1 else 0
        resp = u
        if (item.last().toInt() == resp.roundToInt()) contador++
        println("Real: ${item.last()} \t Calculada: ${resp.roundToInt()}   ${if(item.last().toInt() != resp.roundToInt()) "x" else ""}")
    }
    println("Aciertos: $contador    Efectividad: ${100.0/test.size.toDouble()*contador.toDouble()}")

    /*val d1 = doubleArrayOf(1.0, 0.0, 0.0)
    //val resp: Int
    var u1 = 0.0
    for (i in d1.indices)
        u1 += d1[i] * w[i]
    //resp = if (u1 >= .5) 1 else -1
    print("Respuesta: $u1")*/

}


fun ErrorCuadratico(train: MutableList<DoubleArray>, w: DoubleArray ): Double {
    var lms = 0.0
    for (i in train.indices){
        var u = 0.0
        for (j in w.indices){
            u += w[j] * train[i][j]
        }
        lms += Math.pow(train[i].last() - u, 2.0)
    }
    lms /= train.size
    return lms
}
