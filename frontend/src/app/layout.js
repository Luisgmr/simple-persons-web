import "@/app/globals.css";
import { Rethink_Sans } from "next/font/google";
import { Toaster } from "react-hot-toast";
import { ThemeProvider } from "next-themes";

const rethinkSans = Rethink_Sans({ subsets: ["latin"], weight: ["400","500","700"] });

export const metadata = {
    title: "Cadastro de Pessoa",
    description: "Prova SENAI – Full‑stack Pleno",
};

export default function RootLayout({ children }) {
    return (
        <html lang="pt-BR" suppressHydrationWarning>
        <body className={`min-h-screen bg-background text-foreground ${rethinkSans.className}`}>
        <ThemeProvider attribute="class" defaultTheme="dark" enableSystem>
            {children}
            <Toaster position="top-right" />
        </ThemeProvider>
        </body>
        </html>
    );
}

