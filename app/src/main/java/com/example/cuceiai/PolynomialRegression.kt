package com.example.cuceiai

class PolynomialRegression(private val degree: Int) {
    private lateinit var coefficients: DoubleArray

    // Ajusta el polinomio a los datos dados, recibiendo solo la lista de valores y.
    // Los valores x se generan automáticamente como 1, 2, 3, ...

    fun fit(yValues: List<Double>) {

        val n = degree + 1       // Número de coeficientes del polinomio
        val m = yValues.size     // Número de datos

        val X = Array(m) { DoubleArray(n) }
        val Y = DoubleArray(m)

        // Generar automáticamente los valores de x (1, 2, 3, ...)
        // y llenar las matrices X (matriz de Vandermonde) y Y
        for (i in 0 until m) {
            val x = (i + 1).toDouble()   // x = 1, 2, 3, ...
            for (j in 0 until n) {
                X[i][j] = Math.pow(x, j.toDouble())
            }
            Y[i] = yValues[i]
        }

        // Calcular X^T * X
        val XT_X = Array(n) { DoubleArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                var sum = 0.0
                for (k in 0 until m) {
                    sum += X[k][i] * X[k][j]
                }
                XT_X[i][j] = sum
            }
        }

        // Calcular X^T * Y
        val XT_Y = DoubleArray(n)
        for (i in 0 until n) {
            var sum = 0.0
            for (k in 0 until m) {
                sum += X[k][i] * Y[k]
            }
            XT_Y[i] = sum
        }

        // Resolver sistema XT_X * coef = XT_Y para obtener los coeficientes
        coefficients = gaussianElimination(XT_X, XT_Y)
    }

    // Predice el valor y para un x dado usando los coeficientes calculados
    fun predict(x: Double): Double {
        var yPred = 0.0
        for (i in coefficients.indices) {
            yPred += coefficients[i] * Math.pow(x, i.toDouble())
        }
        return yPred
    }

    // Elimina el sistema lineal (A * x = b) usando eliminación gaussiana
    private fun gaussianElimination(A: Array<DoubleArray>, b: DoubleArray): DoubleArray {
        val n = b.size
        val M = Array(n) { DoubleArray(n) }
        val B = DoubleArray(n)

        // Copiar datos
        for (i in 0 until n) {
            System.arraycopy(A[i], 0, M[i], 0, n)
            B[i] = b[i]
        }

        for (k in 0 until n) {
            // Pivoteo parcial
            var maxRow = k
            var maxVal = Math.abs(M[k][k])
            for (i in k + 1 until n) {
                val currentVal = Math.abs(M[i][k])
                if (currentVal > maxVal) {
                    maxVal = currentVal
                    maxRow = i
                }
            }

            // Intercambiar filas si es necesario
            if (maxRow != k) {
                val tempRow = M[k]
                M[k] = M[maxRow]
                M[maxRow] = tempRow

                val tempVal = B[k]
                B[k] = B[maxRow]
                B[maxRow] = tempVal
            }

            // Si el pivote es cero (o muy pequeño), la matriz es singular
            if (Math.abs(M[k][k]) < 1e-10) {
                // Podrías lanzar una excepción o manejar este caso
                // Aquí agregamos una pequeña constante para evitar singularidad
                M[k][k] = 1e-10
            }

            // Eliminación
            for (i in k + 1 until n) {
                val factor = M[i][k] / M[k][k]
                for (j in k until n) {
                    M[i][j] -= factor * M[k][j]
                }
                B[i] -= factor * B[k]
            }
        }

        // Sustitución hacia atrás
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += M[i][j] * x[j]
            }
            x[i] = (B[i] - sum) / M[i][i]

            // Si aún así obtenemos NaN, devolvemos 0 (o podrías lanzar una excepción)
            if (x[i].isNaN()) {
                x[i] = 0.0
            }
        }

        return x
    }
}
