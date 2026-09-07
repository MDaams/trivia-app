import { AnswerButton } from "./answerButton";
import type { TriviaQuestion } from "../types";

interface QuestionFormProps {
  triviaQuestion: TriviaQuestion;
  answerGiven: boolean;
  correctAnswerValue?: string;
  submittedAnswer?: string;
  onSelectAnswer: (id: string, value: string) => void;
  onNextQuestion: () => void;
  onRetry?: () => void;
  question: string;
}

export function QuestionForm({
  triviaQuestion,
  answerGiven,
  correctAnswerValue,
  submittedAnswer,
  onSelectAnswer,
  onNextQuestion,
  question,
}: QuestionFormProps) {
  const { id, possibleAnswers: answers } = triviaQuestion;

  const isAnswer = (value: string): boolean => value === correctAnswerValue;
  const isSubmittedAnswer = (value: string): boolean =>
    value === submittedAnswer;

  const shouldShowColors = answerGiven;

  return (
    <div className="flex flex-row w-full h-full">
      <div className="flex flex-col gap-4 justify-center items-center flex-1">
        <div className="text-xl font-semibold max-w-2xl">
          <span>{question}</span>
        </div>
        <div className="flex flex-col gap-2 justify-center items-center">
          {answers.map((answer) => (
            <AnswerButton
              key={answer}
              isSubmittedAnswer={shouldShowColors && isSubmittedAnswer(answer)}
              isAnswer={shouldShowColors && isAnswer(answer)}
              disabled={answerGiven}
              onClickCallback={() => onSelectAnswer(id, answer)}
            >
              {answer}
            </AnswerButton>
          ))}
        </div>
      </div>
      <div className="flex items-center">
        <button
          onClick={onNextQuestion}
          disabled={!answerGiven}
          className={`${!answerGiven ? "invisible" : ""} fixed right-24 top-1/2 transform -translate-y-1/2 text-xl leading-none p-0 bg-transparent hover:opacity-70`}
        >
          Next ➡️
        </button>
      </div>
    </div>
  );
}
