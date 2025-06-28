package com.luisgmr.senai.api.mapper;

import org.mapstruct.*;
import com.luisgmr.senai.api.dto.*;
import com.luisgmr.senai.api.domain.*;

@Mapper(componentModel = "spring")
public interface PessoaMapper {
    @Mapping(source = "dataNascimento", target = "nascimento")
    Pessoa toEntity(PessoaRequestDTO dto);

    @Mapping(target = "mensagem", constant = "Operação realizada com sucesso")
    PessoaResponseDTO toResponse(Pessoa pessoa);

    @Mapping(source = "nascimento", target = "dataNascimento")
    @Mapping(source = "criacaoRegistro", target = "dataHoraInclusaoRegistro")
    @Mapping(source = "alteracaoRegistro", target = "dataHoraUltimaAlteracaoRegistro")
    PessoaDetailsDTO toDetails(Pessoa pessoa);
}
