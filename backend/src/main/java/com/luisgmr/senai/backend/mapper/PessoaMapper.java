package com.luisgmr.senai.backend.mapper;

import com.luisgmr.senai.backend.domain.*;
import com.luisgmr.senai.backend.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PessoaMapper {
    @Mapping(source = "dataNascimento", target = "nascimento")
    Pessoa toEntity(PessoaRequestDTO dto);

    @Mapping(target = "mensagem", constant = "Operação realizada com sucesso")
    PessoaResponseDTO toResponse(Pessoa pessoa);

    @Mapping(source = "nascimento", target = "dataNascimento")
    @Mapping(target = "situacaoIntegracao", expression = "java(pessoa.getSituacaoIntegracao().toString())")
    PessoaDetailsDTO toDetails(Pessoa pessoa);

    @Mapping(source = "nascimento", target = "dataNascimento")
    PessoaRequestDTO toRequest(Pessoa pessoa);

}
