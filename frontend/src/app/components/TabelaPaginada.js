"use client";

import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";

const range = (from, to) => Array.from({ length: to - from + 1 }, (_, i) => from + i);

export default function TabelaPaginada({ data = [], columns = [], currentPage, totalPages, onPageChange }) {

    const pageNeighbours = 0;
    const totalNumbers = pageNeighbours * 2 + 3;
    const totalBlocks = totalNumbers + 2;
    let pages = [];
    if (totalPages <= totalBlocks) {
        pages = range(1, totalPages);
    } else {
        const leftBound = Math.max(2, currentPage - pageNeighbours);
        const rightBound = Math.min(totalPages - 1, currentPage + pageNeighbours);
        const showLeftEllipsis = leftBound > 2;
        const showRightEllipsis = rightBound < totalPages - 1;
        pages.push(1);
        if (showLeftEllipsis) pages.push("LEFT");
        pages.push(...range(leftBound, rightBound));
        if (showRightEllipsis) pages.push("RIGHT");
        pages.push(totalPages);
    }

    return (
        <div className="bg-card rounded-lg p-3 w-full border">
            <div className="overflow-x-auto">
                <Table className="w-full text-sm">
                    <TableHeader>
                        <TableRow>
                            {columns.map((col, idx) => (
                                <TableHead
                                    key={idx}
                                    className={col.fill ? "w-full" : "w-fit"}
                                >
                                    {col.header}
                                </TableHead>
                            ))}
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {data.map((item, i) => (
                            <TableRow key={i}>
                                {columns.map((col, cidx) => (
                                    <TableCell key={cidx} className={col.fill ? "w-full" : "w-fit"}>
                                        {col.render(item)}
                                    </TableCell>
                                ))}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            {totalPages > 1 && (
                <div className="flex justify-center mt-4">
                    <Pagination>
                        <PaginationContent>
                            {currentPage > 1 && (
                                <PaginationItem>
                                    <PaginationPrevious onClick={() => onPageChange(currentPage - 1)} />
                                </PaginationItem>
                            )}
                            {pages.map((p, idx) => (
                                p === "LEFT" || p === "RIGHT" ? (
                                    <PaginationItem key={idx}>
                                        <PaginationEllipsis />
                                    </PaginationItem>
                                ) : (
                                    <PaginationItem key={idx}>
                                        <PaginationLink onClick={() => onPageChange(p)} isActive={p === currentPage}>
                                            {p}
                                        </PaginationLink>
                                    </PaginationItem>
                                )
                            ))}
                            {currentPage < totalPages && (
                                <PaginationItem>
                                    <PaginationNext onClick={() => onPageChange(currentPage + 1)} />
                                </PaginationItem>
                            )}
                        </PaginationContent>
                    </Pagination>
                </div>
            )}
        </div>
    );
}
