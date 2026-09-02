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
    <div className="grid grid-rows-3 h-80 gap-0 justify-items-center">
      <div className="text-xl font-semibold flex items-center">
        <span>{question}</span>
      </div>
      <div className="flex flex-col gap-2 justify-center items-center w-full">
        <div>
          <AnswerButton
            isSubmittedAnswer={
              shouldShowColors && isSubmittedAnswer(answers[0])
            }
            isAnswer={shouldShowColors && isAnswer(answers[0])}
            disabled={answerGiven}
            onClickCallback={() => onSelectAnswer(id, answers[0])}
            className="mr-8"
          >
            {answers[0]}
          </AnswerButton>
          <AnswerButton
            isSubmittedAnswer={
              shouldShowColors && isSubmittedAnswer(answers[1])
            }
            isAnswer={shouldShowColors && isAnswer(answers[1])}
            disabled={answerGiven}
            onClickCallback={() => onSelectAnswer(id, answers[1])}
          >
            {answers[1]}
          </AnswerButton>
        </div>

        {answers.length > 2 && (
          <div>
            <AnswerButton
              isSubmittedAnswer={
                shouldShowColors && isSubmittedAnswer(answers[2])
              }
              isAnswer={shouldShowColors && isAnswer(answers[2])}
              disabled={answerGiven}
              onClickCallback={() => onSelectAnswer(id, answers[2])}
              className="mr-8"
            >
              {answers[2]}
            </AnswerButton>
            <AnswerButton
              isSubmittedAnswer={
                shouldShowColors && isSubmittedAnswer(answers[3])
              }
              isAnswer={shouldShowColors && isAnswer(answers[3])}
              disabled={answerGiven}
              onClickCallback={() => onSelectAnswer(id, answers[3])}
            >
              {answers[3]}
            </AnswerButton>
          </div>
        )}
      </div>

      <Button
        className={`mt-2 h-12 ${!answerGiven ? "invisible" : ""}`}
        onClickCallback={onNextQuestion}
      >
        {answerEvaluation} Next Question
      </Button>
    </div>
  );
}
