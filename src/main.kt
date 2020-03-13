import java.io.File
import kotlin.random.Random

const val numEnt = 4
const val numSal = 3

fun main(){

    val salidas: MutableList<DoubleArray> = mutableListOf()

    val train: MutableList<DoubleArray> = mutableListOf()
    File("training.data").forEachLine {
        if (it.isNotEmpty()){
            val tmp = it.split(",")
            train.add(DoubleArray(tmp.size))
            for (i in tmp.indices)
                train.last()[i] = tmp[i].toDouble()
            //--------------
            if(tmp.last().toDouble() == 1.0)
                salidas.add(doubleArrayOf(-1.0, -1.0, 1.0))
            else if(tmp.last().toDouble() == 2.0)
                salidas.add(doubleArrayOf(-1.0, 1.0, -1.0))
            else if(tmp.last().toDouble() == 3.0)
                salidas.add(doubleArrayOf(1.0, -1.0, -1.0))
        }
    }

    //--------------
    /*for (i in train.indices){
        println("Clase: ${train[i].last()} \t Salida: ${salidas[i][0]}, ${salidas[i][1]}, ${salidas[i][2]}")
    }*/
    //--------------

    //****************** ADALINA ******************
    val umbral = 0.00001 //0.00001
    var epoca = 0

    val w = Array(numSal) { DoubleArray(numEnt) }
    val rnd = Random
    //Pesos aleatorios
    for (i in 0 until numSal)
        for (j in 0 until numEnt)
            w[i][j] = rnd.nextDouble()

    var EQMAnterior: DoubleArray
    var EQMAtual: DoubleArray

    //val limite = 10
    var noAprende = 0
    var errProm: Double
    var errPromOld = 0.0

    do {

        EQMAnterior = LMS(train, salidas, w)
        errProm = 0.0
        for (i in 0 until numSal){
            println("Error $i: ${EQMAnterior[i]}")
            errProm += EQMAnterior[i]
        }
        errProm /= EQMAnterior.size
        println("Error Promedio $errProm")
        println("--------------------------------")

        //Busqueda local
        /*for (i in 0 until numSal){
            for (j in 0 until numEnt){
                val r = rnd.nextDouble()
                val o = w[i][j]
                w[i][j] = r
                EQMAtual = LMS(train, salidas, w)

                var nerrProm=0.0
                for (k in 0 until numSal){
                    nerrProm += EQMAtual[k]
                }
                nerrProm /= EQMAtual.size

                if (nerrProm < errProm){
                    w[i][j] = r
                    errProm = nerrProm
                }
                else
                    w[i][j] = o
            }
        }*/
        //-----------------------------

        for (i in train.indices){
            val u = DoubleArray(numSal)
            //val u = doubleArrayOf(-1.0, -1.0, -1.0)
            for (j in 0 until numEnt){
                for (k in 0 until numSal){
                    u[k] += w[k][j] * train[i][j]
                }
            }

            for (j in 0 until numEnt){
                for (k in 0 until numSal){
                    w[k][j] = w[k][j] + umbral * (salidas[i][k] - u[k]) * train[i][j]
                }
            }
        }

        epoca++
        if (errPromOld != errProm){
            errPromOld = errProm
            noAprende = 0
        }
        else
            noAprende++
//
    }while (epoca < 100000)
    //}while (noAprende < 10)

    println("Epocas: $epoca")
    println("--------------------------------")

    //****************** TEST ******************
    val salidaTest: MutableList<DoubleArray> = mutableListOf()
    val test: MutableList<DoubleArray> = mutableListOf()
    File("test.data").forEachLine {
        if (it.isNotEmpty()){
            val tmp = it.split(",")
            test.add(DoubleArray(tmp.size))
            for (i in tmp.indices)
                test.last()[i] = tmp[i].toDouble()
            //-----
            if(tmp.last().toDouble() == 1.0)
                salidaTest.add(doubleArrayOf(-1.0, -1.0, 1.0))
            else if(tmp.last().toDouble() == 2.0)
                salidaTest.add(doubleArrayOf(-1.0, 1.0, -1.0))
            else if(tmp.last().toDouble() == 3.0)
                salidaTest.add(doubleArrayOf(1.0, -1.0, -1.0))
        }
    }

    var sum = 0;
    //var resp: Double
    for (item in test.indices){
        val u = DoubleArray(numSal)
        for (k in 0 until numSal){
            for (i in 0 until numEnt){
                u[k] += test[item][i] * w[k][i]
            }
        }
        //resp = if (u >= 0) 1 else 0
        //resp = u
        //println("Real: ${item.last()} \t Calculada: $resp")
        print("Real (${test[item].last()}): ")
        /*for (k in 0 until numSal){
            print("${salidaTest[item][k]}  ")
        }*/

        //Asignar la clase al valor mayor en la neurona
        var clase = 0.0
        if(u[0] > u[1] && u[0] > u[2])
            /*u = doubleArrayOf(1.0, -1.0, -1.0);*/ clase=3.0
        else if(u[1] > u[0] && u[1] > u[2])
            /*u = doubleArrayOf(-1.0, 1.0, -1.0);*/ clase=2.0
        if(u[2] > u[0] && u[2] > u[1])
            /*u = doubleArrayOf(-1.0, -1.0, 1.0);*/ clase=1.0

        if (test[item].last() == clase) sum++

        print("    Calculada: $clase     ${if(test[item].last() != clase) "x" else ""}")
        /*for (k in 0 until numSal){
            //Escalon
            u[k] = if (u[k]>0) 1.0 else -1.0
            print("${u[k]}  ")
        }*/
        println()
    }

    println("Acieros: $sum    Efectividad: ${100.0/test.size.toDouble()*sum.toDouble()}")

}


fun LMS(train: MutableList<DoubleArray>, salidas: MutableList<DoubleArray>, w: Array<DoubleArray>): DoubleArray {
    val lms = DoubleArray(numSal)
    for (i in train.indices){
        val u = DoubleArray(numSal)
        for (j in 0 until numEnt){
            for (k in 0 until numSal){
                u[k] += w[k][j] * train[i][j]
            }
        }
        for (k in 0 until numSal){
            lms[k] += Math.pow(salidas[i][k] - u[k], 2.0)
        }
    }
    for (k in 0 until numSal){
        lms[k] = lms[k] / train.size
    }
    return lms
}
