/**
 * @vitest-environment jsdom
 */
import { describe, it, expect, vi, afterEach } from "vitest";
import { render, screen, fireEvent, cleanup } from "@testing-library/react";
import { QuestionForm } from "./questionForm";
import type { TriviaQuestion } from "../types";

describe("QuestionForm", () => {
  const mockQuestion: TriviaQuestion = {
    id: "123-abc-uuid",
    question: "What is the capital of France?",
    possibleAnswers: ["London", "Paris", "Berlin", "Madrid"],
  };

  afterEach(() => {
    cleanup();
  });

  it("should render all possible answers", () => {
    render(
      <QuestionForm
        triviaQuestion={mockQuestion}
        answerGiven={false}
        onSelectAnswer={vi.fn()}
        onNextQuestion={vi.fn()}
      />,
    );

    expect(screen.getByText("London")).toBeDefined();
    expect(screen.getByText("Paris")).toBeDefined();
    expect(screen.getByText("Berlin")).toBeDefined();
    expect(screen.getByText("Madrid")).toBeDefined();
  });

  it("should call onSelectAnswer with correct id and value when an answer button is clicked", () => {
    const handleSelectAnswer = vi.fn();

    render(
      <QuestionForm
        triviaQuestion={mockQuestion}
        answerGiven={false}
        onSelectAnswer={handleSelectAnswer}
        onNextQuestion={vi.fn()}
      />,
    );

    const londonButton = screen.getAllByRole("button", { name: "London" })[0];
    fireEvent.click(londonButton);

    expect(handleSelectAnswer).toHaveBeenCalledTimes(1);
    expect(handleSelectAnswer).toHaveBeenCalledWith("123-abc-uuid", "London");
  });

  it("should show the next question button and success text when answer is correct", () => {
    render(
      <QuestionForm
        triviaQuestion={mockQuestion}
        answerGiven={true}
        hasCorrectAnswer={true}
        correctAnswerValue="Paris"
        submittedAnswer="Paris"
        onSelectAnswer={vi.fn()}
        onNextQuestion={vi.fn()}
      />,
    );

    const nextButton = screen.getByRole("button", {
      name: /Amazing! Next Question/i,
    });
    expect(nextButton).toBeDefined();
  });
});
