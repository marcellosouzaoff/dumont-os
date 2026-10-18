import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[3]
GRADLE = ROOT / "experimental/flutter_dumont/android/app/build.gradle.kts"
WORKFLOW = ROOT / ".github/workflows/flutter-preview-validation.yml"


def identity(run_id, attempt):
    return f"br.com.dumont.experience.preview.ghostlabr{run_id}a{attempt}"


class GhostLabCIIdentityTests(unittest.TestCase):
    def test_run_and_attempt_are_valid(self):
        for run_id, attempt in [("39128652741", "1"), ("1", "12")]:
            self.assertRegex(identity(run_id, attempt), r"^br\.com\.dumont\.experience\.preview\.ghostlabr[0-9]+a[0-9]+$")

    def test_new_run_cannot_update_previous_preview(self):
        self.assertNotEqual(identity("39128652741", "1"), identity("39128652742", "1"))

    def test_rerun_cannot_update_previous_preview(self):
        self.assertNotEqual(identity("39128652741", "1"), identity("39128652741", "2"))

    def test_gradle_uses_github_run_id_and_attempt_with_local_fallback(self):
        gradle = GRADLE.read_text()
        self.assertIn('System.getenv("GITHUB_RUN_ID")', gradle)
        self.assertIn('System.getenv("GITHUB_RUN_ATTEMPT")', gradle)
        self.assertIn('.ghostlabr${ciRunId}a${ciAttempt}', gradle)
        self.assertIn('if (ciRunId == null) ".ghostlab" else', gradle)

    def test_workflow_validates_actual_isolated_package(self):
        workflow = WORKFLOW.read_text()
        self.assertIn('ghostlabr${GITHUB_RUN_ID}a${GITHUB_RUN_ATTEMPT}', workflow)
        self.assertNotIn('DUMONT_GHOSTLAB_BUILD_ID', workflow)


if __name__ == "__main__":
    unittest.main()
