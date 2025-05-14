import { useState } from "react";
import { Button } from "@/components/ui/button";
import axios from "axios";

const FallbackTest = () => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

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
        setError("Request timed out after 2 minutes");
      } else {
        setError("An unexpected error occurred");
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
      {error && (
        <p className='text-red-500 mt-4 text-sm' role='alert'>
          {error}
        </p>
      )}
    </div>
  );
};

export default FallbackTest;
