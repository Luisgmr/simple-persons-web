"use client";

import { useState, useEffect } from "react";
import { CalendarIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Input } from "@/components/ui/input";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { ptBR } from "date-fns/locale";

const formatDate = (date) => {
    if (!date) return "";
    return date.toLocaleDateString("pt-BR");
};

export default function DatePickerInput({ onChange, value }) {
    const [open, setOpen] = useState(false);
    const [text, setText] = useState(value ? formatDate(value) : "");
    const [month, setMonth] = useState(value || new Date());

    useEffect(() => {
        if (value) {
            setText(formatDate(value));
            setMonth(value);
        } else {
            setText("");
        }
    }, [value]);

    const handleSelect = (d) => {
        setText(formatDate(d));
        setOpen(false);
        onChange && onChange(d);
    };

    const handleInput = (e) => {
        let v = e.target.value.replace(/\D/g, "").slice(0, 8);
        if (v.length >= 5) v = `${v.slice(0, 2)}/${v.slice(2, 4)}/${v.slice(4)}`;
        else if (v.length >= 3) v = `${v.slice(0, 2)}/${v.slice(2)}`;
        setText(v);
        if (v.length === 10) {
            const [d, m, y] = v.split("/");
            const parsed = new Date(parseInt(y), parseInt(m) - 1, parseInt(d));
            if (!isNaN(parsed.getTime())) {
                onChange && onChange(parsed);
            }
        }
    };

    return (
        <div className="relative w-full">
            <Input value={text} onChange={handleInput} placeholder="dd/MM/aaaa" className="pr-10" />
            <Popover open={open} onOpenChange={setOpen}>
                <PopoverTrigger asChild>
                    <Button variant="ghost" className="absolute top-1/2 right-2 -translate-y-1/2 p-2 h-7 w-7">
                        <CalendarIcon className="h-4 w-4" />
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" alignOffset={-8} sideOffset={10}>
                    <Calendar
                        mode="single"
                        selected={value}
                        onSelect={handleSelect}
                        locale={ptBR}
                        captionLayout="dropdown"
                        month={month}
                        onMonthChange={setMonth}
                        fromYear={1900}
                        toYear={new Date().getFullYear()}
                    />
                </PopoverContent>
            </Popover>
        </div>
    );
}
