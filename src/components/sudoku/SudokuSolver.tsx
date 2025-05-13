import React from "react";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem } from "@/components/ui/form";
import { UseFormReturn } from "react-hook-form"; // Import UseFormReturn
import { z } from "zod";

// Define the schema shape expected by the form
export const FormSchema = z.object({
  board: z.array(z.array(z.number())),
});

interface SudokuSolverProps {
  time: number | null;
  form: UseFormReturn<z.infer<typeof FormSchema>>; // Pass the form instance
  onSubmit: () => void; // The actual submit logic
  isSolving: boolean; // To disable button during solving
  hasBoard: boolean; // To enable/disable solve button
  error: string | null;
}

const SudokuSolver: React.FC<SudokuSolverProps> = ({
  time,
  form,
  onSubmit,
  isSolving,
  hasBoard,
  error,
}) => {
  return (
    <div className='bg-white shadow-md rounded p-6 w-full h-min ring-2 ring-neutral-200'>
      <h2 className='text-xl font-semibold'>Solve Sudoku</h2>
      <div className=' flex flex-col justify-between '>
        <p className='text-neutral-500'>Solve Time</p>
        <span
          className={`text-4xl mt-1 mb-4 font-medium ${
            time === null
              ? "text-neutral-950"
              : (time ?? 0) <= 500
              ? "text-green-400"
              : "text-red-500"
          }`}
        >
          {time ?? "_"} <span className='text-neutral-600'>ms</span>
        </span>
      </div>
      <Form {...form}>
        {/* Use the passed onSubmit handler */}
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <FormField
            control={form.control}
            name='board' // This field is mainly symbolic here, data is set before submit
            render={() => (
              <FormItem>
                <FormControl>
                  <Button
                    type='submit'
                    className='w-full'
                    disabled={!hasBoard || isSolving}
                  >
                    {isSolving ? "Solving..." : "Solve Sudoku Puzzle"}
                  </Button>
                </FormControl>
              </FormItem>
            )}
          />
        </form>
      </Form>
      {error && <p className='text-red-500 mt-4'>{error}</p>}
    </div>
  );
};

export default SudokuSolver;
