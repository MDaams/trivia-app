import { useEffect, useState } from "react";
import { QASkeleton } from "./components/loadingSkeleton";
import { QuestionForm } from "./components/questionForm";
import { evaluateAnswer, fetchQuestion } from "./Api";
import type { TriviaQuestion } from "./types";

function App() {
  const [data, setData] = useState<TriviaQuestion | undefined>();
  const [loading, setLoading] = useState(true);

  const [answerGiven, setAnswerGiven] = useState(false);
  const [hasCorrectAnswer, setHasCorrectAnswer] = useState<
    boolean | undefined
  >();
  const [correctAnswerValue, setCorrectAnswerValue] = useState<
    string | undefined
  >();
  const [submittedAnswer, setSubmittedAnswer] = useState<string | undefined>();

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setLoading(true);
    const result = await fetchQuestion();
    setData(result);
    setLoading(false);
  };

  const selectAnswer = async (id: string, value: string) => {
    const [isCorrect, correctValue, submittedAnswerValue] =
      await evaluateAnswer(id, value);
    setHasCorrectAnswer(isCorrect);
    setCorrectAnswerValue(correctValue);
    setSubmittedAnswer(submittedAnswerValue);
    setAnswerGiven(true);
  };

  const fetchNextQuestion = async () => {
    setLoading(true);
    setAnswerGiven(false);
    setHasCorrectAnswer(undefined);
    setCorrectAnswerValue(undefined);
    setSubmittedAnswer(undefined);

    const result = await fetchQuestion();
    setData(result);
    setLoading(false);
  };

  const generateContent = () => {
    if (loading || !data) return <QASkeleton />;

    return (
      <div className="flex flex-col items-center gap-6 text-center">
        <div className="text-xl font-semibold">
          <span>{data.question}</span>
        </div>

        <QuestionForm
          triviaQuestion={data}
          answerGiven={answerGiven}
          correctAnswerValue={correctAnswerValue}
          submittedAnswer={submittedAnswer}
          hasCorrectAnswer={hasCorrectAnswer}
          onSelectAnswer={selectAnswer}
          onNextQuestion={fetchNextQuestion}
        />
      </div>
    );
  };

  return (
    <section className="flex min-h-screen items-center justify-center bg-gray-50 p-4">
      {generateContent()}
    </section>
  );
}

export default App;
