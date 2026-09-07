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

  describe("Initial State - No Answer Given", () => {
    it("should render the question text", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={false}
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      expect(screen.getByText("What is the capital of France?")).toBeDefined();
    });

    it("should render all possible answers", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
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

    it("should have answer buttons enabled when no answer is given", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={false}
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const buttons = screen.getAllByRole("button").slice(0, 4);
      buttons.forEach((button) => {
        expect(button.hasAttribute("disabled")).toBe(false);
      });
    });

    it("should have invisible next question button", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={false}
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      expect(nextButton.classList.contains("invisible")).toBe(true);
    });

    it("should call onSelectAnswer with correct id and value when an answer button is clicked", () => {
      const handleSelectAnswer = vi.fn();

      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={false}
          onSelectAnswer={handleSelectAnswer}
          onNextQuestion={vi.fn()}
        />,
      );

      const londonButton = screen.getByText("London");
      fireEvent.click(londonButton);

      expect(handleSelectAnswer).toHaveBeenCalledTimes(1);
      expect(handleSelectAnswer).toHaveBeenCalledWith("123-abc-uuid", "London");
    });
  });

  describe("Correct Answer State", () => {
    it("should show Next button when answer is correct", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="Paris"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      expect(nextButton).toBeDefined();
    });

    it("should have visible next question button", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="Paris"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      expect(nextButton.classList.contains("invisible")).toBe(false);
    });

    it("should have answer buttons disabled when answer is given", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="Paris"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const buttons = screen.getAllByRole("button").slice(0, 4);
      buttons.forEach((button) => {
        expect(button.hasAttribute("disabled")).toBe(true);
      });
    });

    it("should call onNextQuestion when next button is clicked", () => {
      const handleNextQuestion = vi.fn();

      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="Paris"
          onSelectAnswer={vi.fn()}
          onNextQuestion={handleNextQuestion}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      fireEvent.click(nextButton);

      expect(handleNextQuestion).toHaveBeenCalledTimes(1);
    });
  });

  describe("Incorrect Answer State", () => {
    it("should show Next button when answer is incorrect", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="London"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      expect(nextButton).toBeDefined();
    });

    it("should have visible next question button", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="London"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const nextButton = screen.getByRole("button", {
        name: /Next/i,
      });
      expect(nextButton.classList.contains("invisible")).toBe(false);
    });

    it("should have answer buttons disabled when answer is given", () => {
      render(
        <QuestionForm
          triviaQuestion={mockQuestion}
          question="What is the capital of France?"
          answerGiven={true}
          correctAnswerValue="Paris"
          submittedAnswer="London"
          onSelectAnswer={vi.fn()}
          onNextQuestion={vi.fn()}
        />,
      );

      const buttons = screen.getAllByRole("button").slice(0, 4);
      buttons.forEach((button) => {
        expect(button.hasAttribute("disabled")).toBe(true);
      });
    });
  });
});
