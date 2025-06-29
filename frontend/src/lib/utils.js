import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import toast from "react-hot-toast";
import { useEffect, useState } from "react";

export function cn(...inputs) {
    return twMerge(clsx(inputs));
}

export function toastError(message) {
    toast.error(message, { duration: 3000 });
}

export function toastSuccess(message) {
    toast.success(message, { duration: 3000 });
}

export function formatCpf(cpf) {
    const digits = cpf.replace(/\D/g, "").slice(0, 11);
    if (digits.length <= 3) return digits;
    if (digits.length <= 6) return `${digits.slice(0, 3)}.${digits.slice(3)}`;
    if (digits.length <= 9)
        return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
}

export function unformatCpf(formatted) {
    return formatted.replace(/\D/g, "").slice(0, 11);
}

export function isValidCpf(cpf) {
    const onlyNums = cpf.replace(/\D/g, "");
    if (onlyNums.length !== 11) return false;
    if (/^(\d)\1{10}$/.test(onlyNums)) return false;
    const calcCheck = (digits, factor) => {
        let total = 0;
        for (let i = 0; i < digits.length; i++) {
            total += Number(digits[i]) * (factor - i);
        }
        const mod = (total * 10) % 11;
        return mod === 10 ? 0 : mod;
    };
    const firstVer = calcCheck(onlyNums.slice(0, 9), 10);
    const secondVer = calcCheck(onlyNums.slice(0, 10), 11);
    return firstVer === Number(onlyNums[9]) && secondVer === Number(onlyNums[10]);
}

export function toTitleCase(str) {
    return str.replace(/\w\S*/g, (txt) =>
        txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
    );
}

export function formatCep(cep) {
    const digits = cep.replace(/\D/g, "").slice(0, 8);
    if (digits.length <= 5) return digits;
    return `${digits.slice(0, 5)}-${digits.slice(5)}`;
}

export function unformatCep(formatted) {
    return formatted.replace(/\D/g, "").slice(0, 8);
}

export function formatDate(date) {
    if (!date) return "";
    const d = new Date(date + 'T00:00:00');
    return d.toLocaleDateString("pt-BR");
}
