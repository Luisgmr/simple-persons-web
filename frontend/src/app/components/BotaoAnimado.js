"use client";

import { motion } from "framer-motion";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function BotaoAnimado({
    tapScale = 0.975,
    hoverScale = 1.025,
    transition = { type: "spring", stiffness: 400, damping: 10 },
    wrapperClassName,
    className,
    ...buttonProps
}) {
    return (
        <motion.div
            className={cn("inline-flex", wrapperClassName)}
            whileTap={{ scale: tapScale }}
            whileHover={{ scale: hoverScale }}
            transition={transition}
        >
            <Button className={cn("transform-none cursor-pointer", className)} {...buttonProps} />
        </motion.div>
    );
}
