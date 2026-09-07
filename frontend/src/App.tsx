import { useEffect, useState } from "react";
import { QASkeleton } from "./components/qaSkeleton";
import { QuestionForm } from "./components/questionForm";
import { evaluateAnswer, fetchQuestion } from "./Api";
import type { TriviaQuestion, EvaluateAnswer } from "./types";
import { ScoreBoard } from "./components/scoreBoard";

// This adds a short delay so the loading skeleton is shown more briefly to prevent stuttering.
// It combines a promise (in this case the api calls) with a short delay.
const withMinimumDelay = async <T,>(
  promise: Promise<T>,
  minDelay: number = 300,
): Promise<T> => {
  const [result] = await Promise.all([
    promise,
    new Promise((resolve) => setTimeout(resolve, minDelay)),
  ]);
  return result;
};

function App() {
  const [data, setData] = useState<TriviaQuestion | undefined>();
  const [loading, setLoading] = useState(true);

  const [answerGiven, setAnswerGiven] = useState(false);
  const [correctAnswerValue, setCorrectAnswerValue] = useState<
    string | undefined
  >();
  const [submittedAnswer, setSubmittedAnswer] = useState<string | undefined>();
  const [lastAttemptedAnswerId, setLastAttemptedAnswerId] = useState<
    string | undefined
  >();
  const [lastAttemptedAnswerValue, setLastAttemptedAnswerValue] = useState<
    string | undefined
  >();

  const [totalAmountOfAnsweredQuestions, setTotalAmountOfAnsweredQuestions] =
    useState<number>(0);
  const [numberOfCorrectQuestions, setNumberOfCorrectQuestions] =
    useState<number>(0);

  const loadQuestion = async () => {
    try {
      const result = await withMinimumDelay(fetchQuestion());
      setData(result);
      return true;
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Failed to load question";
      setData(undefined);
      console.error("Error loading question:", errorMessage);
      return false;
    }
  };

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      await loadQuestion();
      setLoading(false);
      setTotalAmountOfAnsweredQuestions(0);
      setNumberOfCorrectQuestions(0);
    };

    loadInitialData();
  }, []);

  const selectAnswer = async (id: string, value: string) => {
    setLastAttemptedAnswerId(id);
    setLastAttemptedAnswerValue(value);
    try {
      const data: EvaluateAnswer = await withMinimumDelay(
        evaluateAnswer(id, value),
      );

      if (data.error) {
        console.error("Error evaluating answer:", data.error);
        return;
      }

      if (data.isCorrect !== undefined && data.correctAnswer !== undefined) {
        if (data.isCorrect) {
          setNumberOfCorrectQuestions(numberOfCorrectQuestions + 1);
        }
        setTotalAmountOfAnsweredQuestions(totalAmountOfAnsweredQuestions + 1);
        setSubmittedAnswer(value);
        setCorrectAnswerValue(data.correctAnswer);
        setAnswerGiven(true);
      }
    } catch (error) {
      console.error("Error selecting answer:", error);
    }
  };

  const fetchNextQuestion = async () => {
    setLoading(true);
    setAnswerGiven(false);
    setCorrectAnswerValue(undefined);
    setSubmittedAnswer(undefined);

    await loadQuestion();
    setLoading(false);
  };

  const handleRetry = async () => {
    if (!answerGiven) {
      await fetchNextQuestion();
    } else if (
      answerGiven &&
      lastAttemptedAnswerId &&
      lastAttemptedAnswerValue
    ) {
      setLoading(true);
      await selectAnswer(lastAttemptedAnswerId, lastAttemptedAnswerValue);
      setLoading(false);
    }
  };

  const generateContent = () => {
    return (
      <div className="flex flex-col items-center justify-between w-full h-screen">
        {loading ? (
          <div className="flex-1 flex items-center justify-center w-full">
            <QASkeleton />
          </div>
        ) : !data ? (
          <div className="flex-1 flex flex-col items-center justify-center gap-4">
            <div className="text-lg font-semibold text-red-600">
              Failed to load question
            </div>
            <button onClick={handleRetry}>Try Again</button>
          </div>
        ) : (
          <div className="flex-1 flex items-center justify-center w-full">
            <QuestionForm
              triviaQuestion={data}
              answerGiven={answerGiven}
              correctAnswerValue={correctAnswerValue}
              submittedAnswer={submittedAnswer}
              onSelectAnswer={selectAnswer}
              onNextQuestion={fetchNextQuestion}
              onRetry={handleRetry}
              question={data.question}
            />
          </div>
        )}

        <div className="w-full p-8 right-24">
          <ScoreBoard
            numberOfTotalAnsweredQuestions={totalAmountOfAnsweredQuestions}
            numberOfCorrectQuestions={numberOfCorrectQuestions}
          />
        </div>
      </div>
    );
  };

  return (
    <section className="min-h-screen bg-gray-50 p-4">
      {generateContent()}
    </section>
  );
}

export default App;
