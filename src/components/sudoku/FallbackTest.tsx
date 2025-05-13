import { useState } from "react";
import { Button } from "@/components/ui/button";
import axios from "axios";

const FallbackTest = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleTimeoutTest = async () => {
    setIsLoading(true);
    setError(null);

    try {
      // Make a request with a deliberate 2 minute delay
      await axios.post("/api/solve", {
        board: [[1]], // Minimal board for testing
      }, {
        timeout: 10000 // 2 minutes in milliseconds
      });
    } catch (err) {
      setError(
        axios.isAxiosError(err) && err.code === "ECONNABORTED"
          ? "Request timed out after 2 minutes"
          : "An error occurred"
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white shadow-md rounded p-6 w-full h-min ring-2 ring-neutral-200">
      <h2 className="text-xl font-semibold ">Timeout Test</h2>
        <p className='text-neutral-600'>Simulate solving timeout</p>
      <Button
        onClick={handleTimeoutTest}
        disabled={isLoading}
        className="w-full mt-4"
      >
        {isLoading ? "Testing..." : "Test 2 minutes Timeout"}
      </Button>
      {error && (
        <p className="text-red-500 mt-4 text-sm" role="alert">
          {error}
        </p>
      )}
    </div>
  );
};

export default FallbackTest;