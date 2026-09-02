import { Button } from "./button";
import { AnswerButton } from "./answerButton";
import type { TriviaQuestion } from "../types";

interface QuestionFormProps {
  triviaQuestion: TriviaQuestion;
  answerGiven: boolean;
  correctAnswerValue?: string;
  submittedAnswer?: string;
  onSelectAnswer: (id: string, value: string) => void;
  onNextQuestion: () => void;
  hasCorrectAnswer?: boolean;
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
  hasCorrectAnswer,
  question,
}: QuestionFormProps) {
  const { id, possibleAnswers: answers } = triviaQuestion;

  const isAnswer = (value: string): boolean => value === correctAnswerValue;
  const isSubmittedAnswer = (value: string): boolean =>
    value === submittedAnswer;

  const answerEvaluation = hasCorrectAnswer ? "Amazing!" : "Aww!";
  const shouldShowColors = answerGiven;

  return (
    <div className="grid grid-rows-3 h-120 gap-2 justify-items-center">
      <div className="text-xl font-semibold flex items-center">
        <span>{question}</span>
      </div>
      <div className="flex flex-col gap-2 justify-center items-center w-full">
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

      <Button
        className={`mt-8 w-120 ${!answerGiven ? "invisible" : ""}`}
        onClickCallback={onNextQuestion}
      >
        {answerEvaluation} Next Question
      </Button>
    </div>
  );
}
