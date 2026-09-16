package com.cibertec.servicego

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.cibertec.servicego.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding permite conectar el activity_main.xml completo con Kotlin
    // sin usar findViewById. En clase se programará la lógica del Checkpoint 01.
    private lateinit var binding: ActivityMainBinding

    private var siguenteCodigo = 1 //los espaciados deben ser tal cual, antes y luego del igual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Punto de inicio de la clase:
        // aquí se agregarán las llamadas a las funciones del Checkpoint 01.

        mostrarEstadoInicial()
        configurarEventosDeTexto()

        binding.buttonRegistrarServicio.setOnClickListener {
            registrarServicio()
        }
    }

    private  fun  mostrarEstadoInicial(){
        binding.textViewResumenTitulo.text = getString(R.string.resumen_inicial_titulo)
        binding.textViewResumenCliente.text = getString(R.string.resumen_cliente_placeholder)
        binding.textViewResumenDescripcion.text = getString(R.string.resumen_descripcion_placeholder)
        binding.textViewResumenDireccion.text = getString(R.string.resumen_direccion_placeholder)
        binding.textViewEstadoRegistro.text = getString(R.string.estado_pendiente)
        binding.textViewMensajeVisible.text = getString(R.string.mensaje_inicial)
        binding.textViewCodigoPreliminar.text = getString(R.string.codigo_preliminar_formato, generarCodigoCorrelativo())
        binding.textViewCostoEstimado.text = getString(R.string.costo_estimado_formato, getString(R.string.costo_base_inicial))
        binding.textViewIndicadoresTecnicos.text = getString(R.string.indicadores_iniciales)
        binding.buttonRegistrarServicio.isEnabled = false
    }

    private fun configurarEventosDeTexto(){
        binding.editTextCliente.doAfterTextChanged {
            binding.editTextCliente.error = null
            actualizarIndicadoresBasicos()
        }
        binding.editTextDescripcion.doAfterTextChanged {
            binding.editTextDescripcion.error = null
            actualizarIndicadoresBasicos()
        }
        binding.editTextDireccion.doAfterTextChanged {
            binding.editTextDireccion.error = null
            actualizarIndicadoresBasicos()
        }
    }

    private  fun  actualizarIndicadoresBasicos(){
        val cliente = binding.editTextCliente.text.toString().trim()
        val descripcion = binding.editTextDescripcion.text.toString().trim()
        val direccion = binding.editTextDireccion.text.toString().trim()
        val clienteValido = cliente.isNotBlank()

        val descripcionValida = descripcion.length >= 8
        val direccionValida = direccion.isNotBlank()
        val listoParaRegistrar = clienteValido && descripcionValida && direccionValida

        binding.textViewCodigoPreliminar.text = getString(R.string.codigo_preliminar_formato, generarCodigoCorrelativo())

        if (descripcion.isBlank() || direccion.isBlank()){
            //sin dirección y descripción no hay datos suficientes para estimar el servicio
            binding.textViewCostoEstimado.text = getString(R.string.costo_estimado_formato,getString(R.string.costo_base_inicial))
            binding.textViewIndicadoresTecnicos.text = getString(R.string.indicadores_iniciales)
            binding.textViewMensajeVisible.text = getString(R.string.mensaje_inicial)
        } else {
            //la descripcion se clasifica primero y luego se usa para calculcar el costo, tiempo y modalidad con reglas simples
            val tipoServicio = obtenerServicio(descripcion)
            val modalidad = obtenerModalidad(descripcion)
            val costoEstimado = obtenerCostoEstimado(tipoServicio,modalidad)
            val tiempoEstimado = obtenerTiempoEstimado(tipoServicio,modalidad)

            binding.textViewCostoEstimado.text = getString(R.string.costo_estimado_formato, "S/ ${"%.2f".format(costoEstimado)}")
            binding.textViewIndicadoresTecnicos.text = getString(R.string.indicadores_operativos_formato,"%.1f".format(tiempoEstimado),
                tipoServicio,
                modalidad
            )
            binding.textViewMensajeVisible.text = getString(R.string.mensaje_estimacion_previa, tipoServicio.lowercase())

        }

        binding.buttonRegistrarServicio.isEnabled = listoParaRegistrar

        if (listoParaRegistrar){
            binding.textViewEstadoRegistro.text = getString(R.string.estado_listo)
        }else{
            
        }

    }
}
