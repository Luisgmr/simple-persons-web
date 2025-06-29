"use client";

import React, { useState } from "react";
import { Check, ChevronsUpDown } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";

export default function Combobox({
    items = [],
    placeholder = "Selecione…",
    onChange,
    value: controlledValue,
    defaultValue = "",
    width = 200,
}) {
    const [open, setOpen] = useState(false);
    const [value, setValue] = useState(controlledValue || defaultValue);

    React.useEffect(() => {
        if (controlledValue !== undefined) {
            setValue(controlledValue);
        }
    }, [controlledValue]);

    const handleSelect = (val) => {
        const newVal = val === value ? "" : val;
        setValue(newVal);
        setOpen(false);
        onChange && onChange(newVal);
    };

    const label = items.find((i) => i.value === value)?.label;

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={open}
                    className="justify-between"
                    style={{ width }}
                >
                    {label || placeholder}
                    <ChevronsUpDown className="ml-2 h-4 w-4 opacity-50" />
                </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0" style={{ width }}>
                <Command>
                    <CommandInput placeholder="Pesquisar…" className="h-9" />
                    <CommandList>
                        <CommandEmpty>Nada encontrado.</CommandEmpty>
                        <CommandGroup>
                            {items.map((item) => (
                                <CommandItem key={item.value} value={item.value} onSelect={handleSelect}>
                                    {item.label}
                                    <Check className={cn("ml-auto h-4 w-4", value === item.value ? "opacity-100" : "opacity-0")}/>
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    );
}
