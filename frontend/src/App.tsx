import { useEffect, useState } from "react";
import { Button } from "./components/button";
import { QASkeleton } from "./components/loadingSkeleton";
import { mockFetchQuestion, mockValidateAnswer } from "./Api";
import type { QuestionAndAnswer } from "./types";

function App() {
  const [data, setData] = useState<QuestionAndAnswer | undefined>();

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
    const loadInitialData = async () => {
      console.log("Fetching");
      setLoading(true);
      const data = await mockFetchQuestion();
      setData(data);
      setLoading(false);
    };

    loadInitialData();
  }, []);

  const selectAnswer = async (id: number, value: string) => {
    const [isCorrect, correctValue, submittedAnswer] = await mockValidateAnswer(
      id,
      value,
    );
    setHasCorrectAnswer(isCorrect);
    setCorrectAnswerValue(correctValue);
    setSubmittedAnswer(submittedAnswer);
    setAnswerGiven(true);
  };

  const fetchNextQuestion = async () => {
    setLoading(true);
    setAnswerGiven(false);
    setHasCorrectAnswer(false);

    const result = await mockFetchQuestion();
    setData(result);

    setLoading(false);
  };

  const generateNextQuestionButton = () => {
    const answerEvaluation = hasCorrectAnswer ? "Amazing!" : "Aww!";
    return (
      <Button className="mt-2 h-12" onClickCallback={() => fetchNextQuestion()}>
        {answerEvaluation} Next Question
      </Button>
    );
  };

  const isAnswer = (value: string): boolean => {
    return value === correctAnswerValue;
  };

  const isSubmittedAnswer = (value: string): boolean => {
    return value === submittedAnswer;
  };

  const generateAnswerForm = (questionAndAnswer: QuestionAndAnswer) => {
    // Can be either 2 or 4
    const { id, answers } = questionAndAnswer;
    return (
      <div className="grid grid-rows-2">
        <div className="flex flex-col gap-8">
          <div>
            <Button
              isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[0])}
              isAnswer={answerGiven && isAnswer(answers[0])}
              disabled={answerGiven}
              onClickCallback={() => selectAnswer(id, answers[0])}
              className="mr-8"
            >
              {answers[0]}
            </Button>
            <Button
              isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[1])}
              isAnswer={answerGiven && isAnswer(answers[1])}
              disabled={answerGiven}
              onClickCallback={() => selectAnswer(id, answers[1])}
            >
              {answers[1]}
            </Button>
          </div>
          {answers.length > 2 ? (
            <div>
              <Button
                isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[2])}
                isAnswer={answerGiven && isAnswer(answers[2])}
                disabled={answerGiven}
                onClickCallback={() => selectAnswer(id, answers[2])}
                className="mr-8"
              >
                {answers[2]}
              </Button>
              <Button
                isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[3])}
                isAnswer={answerGiven && isAnswer(answers[3])}
                disabled={answerGiven}
                onClickCallback={() => selectAnswer(id, answers[3])}
              >
                {answers[3]}
              </Button>
            </div>
          ) : (
            <></>
          )}
        </div>

        {answerGiven ? generateNextQuestionButton() : <></>}
      </div>
    );
  };

  const generateForm = () => {
    if (loading || !data) return <QASkeleton />;

    return (
      <div className="flex flex-col items-center gap-6 text-center">
        <div className="text-xl font-semibold">
          <span>{data.question}</span>
        </div>

        {generateAnswerForm(data)}
      </div>
    );
  };

  return (
    <>
      <section className="flex min-h-screen items-center justify-center bg-gray-50 p-4">
        {generateForm()}
      </section>
    </>
  );
}

export default App;
