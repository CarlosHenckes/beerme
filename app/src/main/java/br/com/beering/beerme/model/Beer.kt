package br.com.beerme.beerme.model

data class Beer(var id: String?,
                 var rotulo: String,
                 var cervejaria: String,
                 var teorAlcoolico: Float,
                 var pais: String,
                var tipo: String,
                 var urlRotulo: String? )