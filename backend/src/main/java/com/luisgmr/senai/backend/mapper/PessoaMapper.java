package com.luisgmr.senai.backend.mapper;

import com.luisgmr.senai.backend.domain.*;
import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.dto.response.PessoaConsultaResponseDTO;
import com.luisgmr.senai.backend.dto.response.CadastrarPessoaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PessoaMapper {
    @Mapping(source = "dataNascimento", target = "nascimento")
    Pessoa toEntity(CadastrarPessoaRequestDTO dto);

    @Mapping(target = "mensagem", constant = "Operação realizada com sucesso")
    CadastrarPessoaResponseDTO toResponse(Pessoa pessoa);

    @Mapping(source = "nascimento", target = "dataNascimento")
    @Mapping(target = "situacaoIntegracao", expression = "java(pessoa.getSituacaoIntegracao().toString())")
    PessoaConsultaResponseDTO toDetails(Pessoa pessoa);

    @Mapping(source = "nascimento", target = "dataNascimento")
    CadastrarPessoaRequestDTO toRequest(Pessoa pessoa);

}
