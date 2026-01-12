import { useState } from "react";
import { Button } from "@/components/ui/button";
import axios from "axios";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

const FallbackTest = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [showDialog, setShowDialog] = useState(false);

  const handleTimeoutTest = async () => {
    setIsLoading(true);
    setError(null);

    try {
      // Make a request with a deliberate 2 minute delay (120000 ms)
      await axios.post(
        "/api/solves",
        {
          board: [
            [1, 1, 3, 4, 5, 6, 7, 8, 9],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0],
          ],
        },
        { timeout: 10000 }
      );
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(
          "The provided sudoku puzzle is invalid or cannot be solved. The solving algorithm has exceeded the maximum allowed time of 2 minute.."
        );
        setShowDialog(true);
      } else {
        setError("An unexpected error occurred");
        setShowDialog(true);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className='bg-white shadow-md rounded p-6 w-full h-min ring-2 ring-neutral-200'>
      <h2 className='text-xl font-semibold'>Timeout Test</h2>
      <p className='text-neutral-600'>
        Simulate a solving process with a timeout
      </p>
      <Button
        onClick={handleTimeoutTest}
        disabled={isLoading}
        className='w-full mt-4'
      >
        {isLoading ? "Testing..." : "Test 2 Minutes Timeout"}
      </Button>

      <AlertDialog open={showDialog} onOpenChange={setShowDialog}>
        <AlertDialogContent className='border-red-500 bg-red-50'>
          <AlertDialogHeader>
            <AlertDialogTitle className='text-red-600'>Error</AlertDialogTitle>
            <AlertDialogDescription className='text-red-500'>
              {error}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogAction
              onClick={() => setShowDialog(false)}
              className='bg-red-500 hover:bg-red-600 text-white'
            >
              Close
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default FallbackTest;
