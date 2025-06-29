"use client";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "@/lib/api";
import {
    Form,
    FormField,
    FormItem,
    FormLabel,
    FormControl,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { toastError, toastSuccess, formatCpf, unformatCpf, isValidCpf, toTitleCase, formatCep, unformatCep, formatDate } from "@/lib/utils";
import { BotaoAnimado } from '@/app/components/BotaoAnimado';
import DatePickerInput from "@/app/components/DatePickerInput";
import TabelaPaginada from "@/app/components/TabelaPaginada";
import BotaoAlternarTema from "@/app/components/BotaoAlternarTema";
import Combobox from "@/app/components/Combobox";
import { PencilSimple, Trash, MagnifyingGlass, X, CloudArrowUp } from 'phosphor-react';
import { motion } from "framer-motion";
import { Label } from '@/components/ui/label';

const estados = [
    { value: "Acre", label: "Acre" },
    { value: "Alagoas", label: "Alagoas" },
    { value: "Amapá", label: "Amapá" },
    { value: "Amazonas", label: "Amazonas" },
    { value: "Bahia", label: "Bahia" },
    { value: "Ceará", label: "Ceará" },
    { value: "Distrito Federal", label: "Distrito Federal" },
    { value: "Espírito Santo", label: "Espírito Santo" },
    { value: "Goiás", label: "Goiás" },
    { value: "Maranhão", label: "Maranhão" },
    { value: "Mato Grosso", label: "Mato Grosso" },
    { value: "Mato Grosso do Sul", label: "Mato Grosso do Sul" },
    { value: "Minas Gerais", label: "Minas Gerais" },
    { value: "Pará", label: "Pará" },
    { value: "Paraíba", label: "Paraíba" },
    { value: "Paraná", label: "Paraná" },
    { value: "Pernambuco", label: "Pernambuco" },
    { value: "Piauí", label: "Piauí" },
    { value: "Rio de Janeiro", label: "Rio de Janeiro" },
    { value: "Rio Grande do Norte", label: "Rio Grande do Norte" },
    { value: "Rio Grande do Sul", label: "Rio Grande do Sul" },
    { value: "Rondônia", label: "Rondônia" },
    { value: "Roraima", label: "Roraima" },
    { value: "Santa Catarina", label: "Santa Catarina" },
    { value: "São Paulo", label: "São Paulo" },
    { value: "Sergipe", label: "Sergipe" },
    { value: "Tocantins", label: "Tocantins" }
];

const schema = z.object({
    nome: z.string().min(1, "Nome é obrigatório")
        .refine(val => val.trim().split(' ').length > 1, "Nome deve ter mais de 1 nome")
        .transform(toTitleCase),
    dataNascimento: z.date().optional().refine(date => !date || date <= new Date(), "Data não pode ser maior que hoje"),
    cpf: z.string().optional().or(z.literal(""))
        .refine(val => !val || (val.length === 11 && isValidCpf(val)), "CPF inválido"),
    email: z.string().optional().or(z.literal(""))
        .refine(val => !val || z.string().email().safeParse(val).success, "Email inválido"),
    cep: z.string().optional().or(z.literal("")),
    rua: z.string().optional().or(z.literal("")),
    numero: z.union([z.string(), z.number()]).optional().transform(val => val ? String(val) : ""),
    cidade: z.string().optional().or(z.literal("")),
    estado: z.string().optional().or(z.literal("")),
}).refine(data => {
    if (data.cep) {
        return data.rua && data.numero && data.cidade && data.estado;
    }
    return true;
}, {
    message: "Se CEP informado, todos campos do endereço são obrigatórios",
    path: ["cep"]
});

export default function Page() {
    const [pessoas, setPessoas] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [editing, setEditing] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [toDelete, setToDelete] = useState(null);
    const [cpfConsulta, setCpfConsulta] = useState("");
    const [pessoaIntegrada, setPessoaIntegrada] = useState(null);

    const form = useForm({
        resolver: zodResolver(schema),
        defaultValues: {
            nome: "",
            dataNascimento: undefined,
            cpf: "",
            email: "",
            cep: "",
            rua: "",
            numero: "",
            cidade: "",
            estado: "",
        },
    });

    const fetchData = async (p = page) => {
        try {
            const { data } = await axios.get("/pessoa/paginado", { params: { pagina: p, tamanho: 10 } });
            setPessoas(data.content || []);
            setTotalPages(data.totalPages || 1);
        } catch (e) {
            toastError("Erro ao buscar pessoas");
        }
    };

    const buscarCep = async () => {
        const cep = form.getValues("cep");
        if (!cep || cep.length !== 8) {
            toastError("CEP deve ter 8 dígitos");
            return;
        }
        try {
            const data = await fetch(`https://viacep.com.br/ws/${cep}/json/`).then(r => r.json());
            if (data.erro) {
                toastError("CEP não encontrado");
                return;
            }
            form.setValue("rua", data.logradouro || "");
            form.setValue("cidade", data.localidade || "");
            form.setValue("estado", data.estado || "");
            toastSuccess("Endereço encontrado");
        } catch (e) {
            toastError("Erro ao buscar CEP");
        }
    };

    const integrarPessoa = async (cpf) => {
        try {
            await axios.post(`/pessoa/cpf/${cpf}/integrar`);
            toastSuccess("Pessoa enviada para integração");
            await fetchData();
        } catch (e) {
            toastError(e.response?.data?.mensagem || "Erro ao integrar pessoa");
        }
    };

    const confirmarExclusao = (pessoa) => {
        setToDelete(pessoa);
        setDialogOpen(true);
    };

    const excluirPessoa = async () => {
        if (!toDelete) return;
        try {
            await axios.delete(`/pessoa/${toDelete.idPessoa}`);
            toastSuccess("Pessoa excluída com sucesso");
            if (editing?.id === toDelete.id) setEditing(null);
            await fetchData();
        } catch (e) {
            toastError(e.response?.data?.mensagem || "Erro ao excluir pessoa");
        } finally {
            setDialogOpen(false);
            setToDelete(null);
        }
    };

    const consultarPessoaIntegrada = async () => {
        if (!cpfConsulta || cpfConsulta.length !== 11) {
            toastError("CPF deve ter 11 dígitos");
            return;
        }
        try {
            const { data } = await axios.get(`/pessoa/cpf/${cpfConsulta}/integrada`);
            setPessoaIntegrada(data);
            toastSuccess("Pessoa integrada encontrada");
        } catch (e) {
            toastError(e.response?.data?.mensagem || "Pessoa integrada não encontrada");
            setPessoaIntegrada(null);
        }
    };

    const formatDateTime = (dateTime) => {
        if (!dateTime) return "";
        return new Date(dateTime).toLocaleString("pt-BR");
    };

    const formatDateTimeWithDefault = (dateTime) => {
        if (!dateTime) return "Nunca alterado";
        return new Date(dateTime).toLocaleString("pt-BR");
    };

    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        if (editing) {
            form.reset({
                nome: editing.nome,
                dataNascimento: editing.dataNascimento ? new Date(editing.dataNascimento + 'T00:00:00') : undefined,
                cpf: editing.cpf || "",
                email: editing.email || "",
                cep: editing.endereco?.cep || "",
                rua: editing.endereco?.rua || "",
                numero: editing.endereco?.numero ? String(editing.endereco.numero) : "",
                cidade: editing.endereco?.cidade || "",
                estado: editing.endereco?.estado || "",
            });
        }
    }, [editing, form]);

    const onSubmit = async (values) => {
        try {
            const payload = {
                nome: values.nome,
                dataNascimento: values.dataNascimento?.toISOString().split("T")[0],
                cpf: values.cpf,
                email: values.email,
                endereco: {
                    cep: values.cep,
                    rua: values.rua,
                    numero: values.numero ? parseInt(values.numero) : null,
                    cidade: values.cidade,
                    estado: values.estado,
                },
            };

            if (editing) {
                await axios.put(`/pessoa/${editing.idPessoa}`, payload);
            } else {
                await axios.post("/pessoa", payload);
            }

            toastSuccess(editing ? "Pessoa atualizada com sucesso" : "Pessoa cadastrada com sucesso");
            form.reset({
                nome: "",
                dataNascimento: undefined,
                cpf: "",
                email: "",
                cep: "",
                rua: "",
                numero: "",
                cidade: "",
                estado: "",
            });
            setEditing(null);
            fetchData();
        } catch (e) {
            toastError(e.response?.data?.mensagem || "Erro ao salvar");
        }
    };

    const columns = [
        { header: "Nome", render: (p) => p.nome, fill: true },
        { header: "Nascimento", render: (p) => formatDate(p.dataNascimento) },
        { header: "CPF", render: (p) => formatCpf(p.cpf) },
        { header: "Cidade", render: (p) => `${p.endereco?.cidade} / ${p.endereco?.estado}` },
        { header: "Situação", render: (p) => p.situacaoIntegracao },
        {
            header: "Ação",
            render: (p) => (
                <div className="flex gap-2">
                    <BotaoAnimado size="icon" variant="ghost" title="Editar" onClick={() => setEditing(p)}>
                        <PencilSimple size={16} weight={"bold"}/>
                    </BotaoAnimado>
                    {(p.situacaoIntegracao === "Pendente" || p.situacaoIntegracao === "Erro") && (
                        <BotaoAnimado size="icon" variant="ghost" title="Integrar" onClick={() => integrarPessoa(p.cpf)}>
                            <CloudArrowUp size={16} weight={"bold"}/>
                        </BotaoAnimado>
                    )}
                    <BotaoAnimado size="icon" variant="ghost" title="Excluir" onClick={() => confirmarExclusao(p)}>
                        <Trash size={16} weight={"bold"}/>
                    </BotaoAnimado>
                </div>
            ),
        },
    ];

    return (
        <div className="container mx-auto py-10 space-y-10">
            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar exclusão</DialogTitle>
                        <DialogDescription>
                            Deseja realmente excluir "{toDelete?.nome}"? Esta ação não pode ser desfeita.
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <BotaoAnimado variant="outline" onClick={() => setDialogOpen(false)}>
                            Cancelar
                        </BotaoAnimado>
                        <BotaoAnimado variant="destructive" onClick={excluirPessoa}>
                            Excluir
                        </BotaoAnimado>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            <BotaoAlternarTema />

            {editing && (
                <motion.div
                    className="p-4 border rounded-lg relative bg-blue-50 dark:bg-blue-950/30"
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    transition={{ type: "spring", stiffness: 500, damping: 25 }}
                >
                    <button
                        className="absolute top-2 right-2 text-muted-foreground hover:text-foreground"
                        onClick={() => {
                            setEditing(null);
                            form.reset({
                                nome: "",
                                dataNascimento: undefined,
                                cpf: "",
                                email: "",
                                cep: "",
                                rua: "",
                                numero: "",
                                cidade: "",
                                estado: "",
                            });
                        }}
                    >
                        <X size={20} />
                    </button>
                    <p className="font-medium text-sm text-muted-foreground">Editando:</p>
                    <p className="font-semibold">
                        {editing.nome} {editing.cpf && `— ${formatCpf(editing.cpf)}`}
                    </p>
                </motion.div>
            )}

            <div className="rounded-lg border p-6 space-y-4 bg-card">
                <h2 className="text-lg font-semibold">Cadastro de Pessoa</h2>
                <Form {...form}>
                    <form className="grid md:grid-cols-2 gap-4" onSubmit={form.handleSubmit(onSubmit)}>
                        <FormField
                            control={form.control}
                            name="nome"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Nome</FormLabel>
                                    <FormControl>
                                        <Input {...field} placeholder={"João Silva"} onChange={(e) => field.onChange(toTitleCase(e.target.value.replace(/\d/g, "")))} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="cpf"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>CPF</FormLabel>
                                    <FormControl>
                                        <Input
                                            placeholder={"000.000.000-00"}
                                            value={formatCpf(field.value || "")}
                                            onChange={(e) => field.onChange(unformatCpf(e.target.value))}
                                            inputMode="numeric"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="dataNascimento"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Data de nascimento</FormLabel>
                                    <FormControl>
                                        <DatePickerInput value={field.value} onChange={field.onChange} />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        <FormField
                            control={form.control}
                            name="email"
                            render={({ field }) => (
                                <FormItem className="col-span-full md:col-span-1">
                                    <FormLabel>Email</FormLabel>
                                    <FormControl>
                                        <Input placeholder={"joaosilva@gmail.com"} {...field} type="email" />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />

                        {/* Endereço bloco */}
                        <div className="col-span-full grid md:grid-cols-3 gap-4 pt-4">
                            <FormField
                                control={form.control}
                                name="cep"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>CEP</FormLabel>
                                        <FormControl>
                                            <div className="flex gap-2 w-full">
                                                <Input
                                                    value={formatCep(field.value || "")}
                                                    onChange={(e) => field.onChange(unformatCep(e.target.value))}
                                                    inputMode="numeric"
                                                    maxLength={9}
                                                    placeholder="00000-000"
                                                    className="flex-1"
                                                />
                                                <BotaoAnimado
                                                    type="button"
                                                    variant="outline"
                                                    size="icon"
                                                    onClick={buscarCep}
                                                    className="shrink-0"
                                                >
                                                    <MagnifyingGlass size={16} />
                                                </BotaoAnimado>
                                            </div>
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="rua"
                                render={({ field }) => (
                                    <FormItem className="md:col-span-2">
                                        <FormLabel>Rua</FormLabel>
                                        <FormControl>
                                            <Input placeholder={"Rua do Senai"} {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="cidade"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Cidade</FormLabel>
                                        <FormControl>
                                            <Input placeholder={"Tubarão"} {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="estado"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Estado</FormLabel>
                                        <FormControl>
                                            <Combobox
                                                items={estados}
                                                placeholder="Selecione o estado"
                                                value={field.value}
                                                onChange={field.onChange}
                                                width="100%"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="numero"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Número</FormLabel>
                                        <FormControl>
                                            <Input
                                                placeholder={"123"}
                                                {...field}
                                                inputMode="numeric"
                                                onChange={(e) => field.onChange(e.target.value.replace(/\D/g, ""))}
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>

                        <div className="col-span-full flex justify-start pt-2">
                            <BotaoAnimado type="submit" className={"px-9"}>{editing ? "Salvar" : "Cadastrar"}</BotaoAnimado>
                        </div>
                    </form>
                </Form>
            </div>

            <div className="rounded-lg border p-6 bg-card">
                <h2 className="text-lg font-semibold mb-4">Pessoas Cadastradas</h2>
                <TabelaPaginada
                    data={pessoas}
                    columns={columns}
                    currentPage={page + 1}
                    totalPages={totalPages}
                    onPageChange={(p) => {
                        setPage(p - 1);
                        fetchData(p - 1);
                    }}
                />
            </div>

            <div className="rounded-lg border p-6 bg-card">
                <h2 className="text-lg font-semibold mb-4">Consultar pessoas integradas</h2>
                <div className={"flex flex-col gap-2"}>
                    <Label>CPF</Label>
                    <div className="flex gap-2 mb-4">
                        <Input
                            value={formatCpf(cpfConsulta)}
                            onChange={(e) => setCpfConsulta(unformatCpf(e.target.value))}
                            placeholder="000.000.000-00"
                            inputMode="numeric"
                            className="w-fit"
                        />
                        <BotaoAnimado onClick={consultarPessoaIntegrada}>
                            Consultar
                        </BotaoAnimado>
                    </div>
                </div>

                {pessoaIntegrada && (
                    <div className="space-y-2 p-4 bg-muted rounded-lg">
                        <p><strong>Nome:</strong> {pessoaIntegrada.nome}</p>
                        <p><strong>Nascimento:</strong> {formatDate(pessoaIntegrada.dataNascimento)}</p>
                        <p><strong>Situação da Integração:</strong> {pessoaIntegrada.situacaoIntegracao}</p>
                        <p><strong>Data/Hora da Inclusão:</strong> {formatDateTime(pessoaIntegrada.dataHoraInclusao)}</p>
                        <p><strong>Data/Hora da última alteração:</strong> {formatDateTimeWithDefault(pessoaIntegrada.dataHoraUltimaAlteracao)}</p>
                    </div>
                )}
            </div>
        </div>
    );
}
